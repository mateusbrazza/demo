import java.util.*;
import java.util.stream.Collectors;

public class RulesOpinV2 extends PermissionsValidationV2 {

    public void validateConsent(OpinExternalRequestV2 req, Map<String, String> headers) {
        RulesCommon.validateHeaderFapiInteractionId(headers);
        validateEmptyPermissions(req);

        List<String> permissions = req.getData().getPermissions();
        validatePermissionFormat(permissions);
        validateOnlyValidPermissions(permissions);
        validateMinimumPermissionCount(permissions);
        validateNoMixPFandPJ(permissions);

        Set<String> permissionSet = new HashSet<>(permissions);

        if (PermissionCatalog.hasFase2AndFase3(permissionSet)) {
            throw new PermissionsValidationException("Não é permitido misturar permissões de Fase 2 com Fase 3.");
        }

        if (!PermissionCatalog.isFase3GroupValid(permissionSet)) {
            throw new PermissionsValidationException("Agrupamento de permissões da Fase 3 incompleto.");
        }

        if (PermissionCatalog.hasMissingFase2Obligatory(permissionSet)) {
            throw new PermissionsValidationException("Permissão obrigatória RESOURCES_READ ausente.");
        }

        if (PermissionCatalog.hasOnlyResourcesRead(permissionSet)) {
            throw new PermissionsValidationException("RESOURCES_READ não pode ser enviada sozinha.");
        }

        validateSinglePhase3Group(permissions);

        if (PermissionCatalog.isOnlyFase2(permissionSet)) {
            validateFunctionalGroupRules(permissions);
        }
    }

    private void validateSinglePhase3Group(List<String> permissions) {
        Set<String> groups = permissions.stream()
            .flatMap(p -> PermissionCatalog.FASE3_REQUIRED_BY_GROUP.entrySet().stream()
                .filter(entry -> entry.getValue().contains(p))
                .map(Map.Entry::getKey))
            .collect(Collectors.toSet());

        if (groups.size() > 1) {
            throw new PermissionsValidationException("Só é permitido enviar permissões de um único agrupamento da Fase 3.");
        }
    }

    protected void validateFunctionalGroupRules(List<String> permissions) {
        Set<String> set = new HashSet<>(permissions);
        if (!PermissionCatalog.hasAllPermissionsOfGroup(set, "CREDIT")) {
            throw new PermissionsValidationException("Permissão obrigatória do grupo de crédito ausente.");
        }
        if (!PermissionCatalog.hasAllPermissionsOfGroup(set, "INVESTMENTS")) {
            throw new PermissionsValidationException("Permissão obrigatória do grupo de investimentos ausente.");
        }
        if (!PermissionCatalog.hasAllPermissionsOfGroup(set, "EXCHANGES")) {
            throw new PermissionsValidationException("Permissão obrigatória do grupo de câmbio ausente.");
        }
    }

    protected void validateNoMixPFandPJ(List<String> permissions) {
        boolean hasPF = permissions.stream().anyMatch(PermissionCatalog.PF::contains);
        boolean hasPJ = permissions.stream().anyMatch(PermissionCatalog.PJ::contains);
        if (hasPF && hasPJ) {
            throw new PermissionsValidationException("Não é permitido misturar permissões de PF e PJ.");
        }
    }
}