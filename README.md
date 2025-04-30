# demo
public class RulesOpinV2 extends PermissionsValidationV2 {

    public void validateConsent(ConsentRequestV2 req, String brandCode) {
        validateEmptyPermissions(req);

        List<String> permissions = req.getData().getPermissions();
        validatePermissionFormat(permissions);
        validateOnlyValidPermissions(permissions);
        validateMinimumPermissionCount(permissions);
        validateNoMixPFandPJ(permissions);
        validateBaseDependencyRules(permissions);
        validateExpirationDate(req.getData().getExpirationDateTime());
        validateNoMixedPhases(permissions);
        validateSinglePhase3Group(permissions);

        // Aplica regras de Fase 2 apenas se só houver permissões de Fase 2
        boolean isOnlyPhase2 = permissions.stream().allMatch(PermissionCatalog.DATA_SHARING_PERMISSIONS::contains);
        if (isOnlyPhase2) {
            validateFunctionalGroupRules(permissions);
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

    protected void validateBaseDependencyRules(List<String> permissions) {
        validateGroupBaseRule(permissions, PermissionCatalog.ACCOUNT_BASE, PermissionCatalog.ACCOUNTS);
        validateGroupBaseRule(permissions, PermissionCatalog.CREDIT_CARD_BASE, PermissionCatalog.CREDIT_CARD_BILLS);
    }

    protected void validateFunctionalGroupRules(List<String> permissions) {
        validateHasRequiredGroupPermissions(permissions, PermissionCatalog.CREDIT_REQUIRED);
        validateHasRequiredGroupPermissions(permissions, PermissionCatalog.INVESTMENTS_REQUIRED);
        validateHasRequiredGroupPermissions(permissions, PermissionCatalog.EXCHANGES_REQUIRED);
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
