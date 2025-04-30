package br.com.seuprojeto.consents.rules;

import br.com.seuprojeto.consents.dto.request.CreateConsentRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RulesOpinV2 extends PermissionsValidationV2 {

    public void validateConsent(CreateConsentRequest req, String brandCode) {
        String documentType = null;
        if (req.getData().getLoggedUser() != null && req.getData().getLoggedUser().getDocument() != null) {
            documentType = req.getData().getLoggedUser().getDocument().getIdentification(); // CPF
        } else if (req.getData().getBusinessEntity() != null && req.getData().getBusinessEntity().getDocument() != null) {
            documentType = req.getData().getBusinessEntity().getDocument().getIdentification(); // CNPJ
        }

        validateBrandActive(brandCode, documentType);
        validateEmptyPermissions(req);

        List<String> permissions = req.getData().getPermissions();
        validatePermissionFormat(permissions);
        validateOnlyValidPermissions(permissions);
        validateMinimumPermissionCount(permissions);
        validateNoMixPFandPJ(permissions);
        validateFunctionalGroupRules(permissions);
        validateBaseDependencyRules(permissions);
        validateExpirationDate(req.getData().getExpirationDateTime());

        validateNoMixedPhases(permissions);
        validateSinglePhase3Group(permissions);
    }

    protected void validateBrandActive(String brandCode, String documentType) {
        BrandEnum brand = BrandEnum.getByBrandCode(brandCode);
        if (brand == null || !brand.isActiveFor(documentType)) {
            throw new BrandValidationException("Marca inativa ou inválida para o tipo: " + documentType);
        }
    }


    protected void validateExpirationDate(String expirationDateTime) {
        if (expirationDateTime == null || expirationDateTime.isBlank()) {
            throw new DateTimeValidationException("Data de expiração não informada.");
        }

        LocalDateTime expiration = LocalDateTime.parse(expirationDateTime);
        if (expiration.isBefore(LocalDateTime.now())) {
            throw new DateTimeValidationException("Data de expiração está no passado.");
        }

        if (expiration.isAfter(LocalDateTime.now().plusYears(1))) {
            throw new DateTimeValidationException("Data de expiração excede o limite de 1 ano.");
        }
    }

    protected void validateFunctionalGroupRules(List<String> permissions) {
        validateHasRequiredGroupPermissions(permissions, PermissionCatalog.CREDIT_REQUIRED);
        validateHasRequiredGroupPermissions(permissions, PermissionCatalog.INVESTMENTS_REQUIRED);
        validateHasRequiredGroupPermissions(permissions, PermissionCatalog.EXCHANGES_REQUIRED);
    }

    protected void validateBaseDependencyRules(List<String> permissions) {
        validateGroupBaseRule(permissions, PermissionCatalog.ACCOUNT_BASE, PermissionCatalog.ACCOUNTS);
        validateGroupBaseRule(permissions, PermissionCatalog.CREDIT_CARD_BASE, PermissionCatalog.CREDIT_CARD_BILLS);
    }

    private void validateNoMixedPhases(List<String> permissions) {
        boolean hasPhase2 = permissions.stream().anyMatch(PermissionCatalog.DATA_SHARING_PERMISSIONS::contains);
        boolean hasPhase3 = permissions.stream().anyMatch(PermissionCatalog.SERVICE_INITIATION_PERMISSIONS::contains);

        if (hasPhase2 && hasPhase3) {
            throw new PermissionsValidationException("Não é permitido misturar permissões de Fase 2 com Fase 3.");
        }
    }

    private void validateSinglePhase3Group(List<String> permissions) {
        Set<String> groups = permissions.stream()
                .filter(PermissionCatalog.SERVICE_INITIATION_PERMISSIONS::contains)
                .map(p -> PermissionCatalog.SERVICE_INITIATION_GROUPS.get(p))
                .collect(Collectors.toSet());

        if (groups.size() > 1) {
            throw new PermissionsValidationException("Só é permitido enviar permissões de um único agrupamento da Fase 3.");
        }
    }
}
