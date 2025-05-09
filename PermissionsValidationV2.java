import java.util.*;

public class PermissionsValidationV2 {

    protected void validateEmptyPermissions(OpinExternalRequestV2 req) {
        if (req.getData().getPermissions() == null || req.getData().getPermissions().isEmpty()) {
            throw new PermissionsValidationException("A lista de permissões está vazia.");
        }
    }

    protected void validatePermissionFormat(List<String> permissions) {
        boolean hasInvalid = permissions.stream()
            .anyMatch(p -> !p.equals(p.trim()) || !p.equals(p.toUpperCase()));
        if (hasInvalid) {
            throw new PermissionsValidationException("Permissões devem estar em letras maiúsculas e sem espaços.");
        }
    }

    protected void validateMinimumPermissionCount(List<String> permissions) {
        if (permissions.size() < 1) {
            throw new PermissionsValidationException("Deve haver pelo menos uma permissão.");
        }
    }

    protected void validateOnlyValidPermissions(List<String> permissions) {
        Set<String> validPermissions = new HashSet<>();
        validPermissions.addAll(PermissionCatalog.FASE2_PERMISSOES);
        PermissionCatalog.FASE3_REQUIRED_BY_GROUP.values().forEach(validPermissions::addAll);

        permissions.stream()
            .filter(p -> !validPermissions.contains(p))
            .findAny()
            .ifPresent(invalid -> {
                throw new PermissionsValidationException("Permissão inválida: " + invalid);
            });
    }
}