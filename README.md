public class br.com.seuprojeto.consents.rules.v2.PermissionCatalog {

    // Permissões de compartilhamento de dados (Fase 2)
    public static final Set<String> DATA_SHARING_PERMISSIONS = Set.of(
        "CUSTOMERS_PERSONAL_IDENTIFICATIONS_READ",
        "CUSTOMERS_BUSINESS_IDENTIFICATIONS_READ",
        "CAPITALIZATION_TITLE_READ",
        "PENSION_PLAN_READ",
        "LIFE_PENSION_READ",
        "FINANCIAL_ASSISTANCE_READ",
        "DAMAGES_AND_PEOPLE_AUTO_READ",
        "RESOURCES_READ"
        // Adicione todas as outras permissões de Fase 2 conforme necessário
    );

    // Permissões de iniciação de serviços (Fase 3)
    public static final Map<String, String> SERVICE_INITIATION_GROUPS = Map.ofEntries(
        Map.entry("CLAIM_NOTIFICATION_REQUEST_DAMAGE_CREATE", "SINISTRO"),
        Map.entry("CLAIM_NOTIFICATION_REQUEST_PERSON_CREATE", "SINISTRO"),
        Map.entry("ENDORSEMENT_REQUEST_CREATE", "ENDOSSO"),
        Map.entry("QUOTE_AUTO_LEAD_CREATE", "COTACAO_AUTO"),
        Map.entry("QUOTE_HOUSING_LEAD_CREATE", "COTACAO_HABITACIONAL")
        // Adicione todos os mapeamentos conforme a documentação
    );

    public static final Set<String> SERVICE_INITIATION_PERMISSIONS = SERVICE_INITIATION_GROUPS.keySet();

    public static final List<String> ACCOUNT_BASE = List.of("ACCOUNTS_READ");
    public static final List<String> ACCOUNTS = List.of("ACCOUNTS_TRANSACTIONS_READ");
    public static final List<String> CREDIT_CARD_BASE = List.of("CREDIT_CARDS_READ");
    public static final List<String> CREDIT_CARD_BILLS = List.of("CREDIT_CARDS_BILLS_READ");
    public static final Set<String> PF = Set.of("CUSTOMERS_PERSONAL_IDENTIFICATIONS_READ", "ACCOUNTS_READ");
    public static final Set<String> PJ = Set.of("CUSTOMERS_BUSINESS_IDENTIFICATIONS_READ");
    public static final List<String> CREDIT_REQUIRED = List.of("CREDIT_OPERATIONS_READ");
    public static final List<String> INVESTMENTS_REQUIRED = List.of("INVESTMENTS_READ");
    public static final List<String> EXCHANGES_REQUIRED = List.of("EXCHANGES_READ");
}

public class br.com.seuprojeto.consents.rules.v2.PermissionsValidationV2 {

    protected void validateEmptyPermissions(ConsentRequestV2 req) {
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
        validPermissions.addAll(br.com.seuprojeto.consents.rules.v2.PermissionCatalog.DATA_SHARING_PERMISSIONS);
        validPermissions.addAll(br.com.seuprojeto.consents.rules.v2.PermissionCatalog.SERVICE_INITIATION_PERMISSIONS);

        permissions.stream()
            .filter(p -> !validPermissions.contains(p))
            .findAny()
            .ifPresent(invalid -> {
                throw new PermissionsValidationException("Permissão inválida: " + invalid);
            });
    }

    protected void validateHasRequiredGroupPermissions(List<String> permissions, List<String> requiredGroup) {
        if (!permissions.containsAll(requiredGroup)) {
            throw new PermissionsValidationException("Está faltando permissão obrigatória do grupo: " + requiredGroup);
        }
    }

    protected void validateGroupBaseRule(List<String> permissions, List<String> base, List<String> dependents) {
        boolean hasDependent = permissions.stream().anyMatch(dependents::contains);
        boolean hasBase = permissions.stream().anyMatch(base::contains);

        if (hasDependent && !hasBase) {
            throw new PermissionsValidationException("Permissão base ausente para permissões dependentes.");
        }
    }

    protected void validateNoMixPFandPJ(List<String> permissions) {
        boolean hasPF = permissions.stream().anyMatch(br.com.seuprojeto.consents.rules.v2.PermissionCatalog.PF::contains);
        boolean hasPJ = permissions.stream().anyMatch(br.com.seuprojeto.consents.rules.v2.PermissionCatalog.PJ::contains);
        if (hasPF && hasPJ) {
            throw new PermissionsValidationException("Não é permitido misturar permissões de PF e PJ.");
        }
    }
}

public class br.com.seuprojeto.consents.rules.v2.RulesOpinV2 extends br.com.seuprojeto.consents.rules.v2.PermissionsValidationV2 {

    public void validateConsent(ConsentRequestV2 req, String brandCode) {
        validateBrandActive(brandCode, req.getData().getBusinessType());
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

    protected void validateBrandActive(String brandCode, BusinessType businessType) {
        BrandEnum brand = BrandEnum.getByBrandCode(brandCode);
        if (brand == null || !brand.isActiveFor(businessType)) {
            throw new BrandValidationException("Marca inativa ou inválida para o tipo: " + businessType);
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
        validateHasRequiredGroupPermissions(permissions, br.com.seuprojeto.consents.rules.v2.PermissionCatalog.CREDIT_REQUIRED);
        validateHasRequiredGroupPermissions(permissions, br.com.seuprojeto.consents.rules.v2.PermissionCatalog.INVESTMENTS_REQUIRED);
        validateHasRequiredGroupPermissions(permissions, br.com.seuprojeto.consents.rules.v2.PermissionCatalog.EXCHANGES_REQUIRED);
    }

    protected void validateBaseDependencyRules(List<String> permissions) {
        validateGroupBaseRule(permissions, br.com.seuprojeto.consents.rules.v2.PermissionCatalog.ACCOUNT_BASE, br.com.seuprojeto.consents.rules.v2.PermissionCatalog.ACCOUNTS);
        validateGroupBaseRule(permissions, br.com.seuprojeto.consents.rules.v2.PermissionCatalog.CREDIT_CARD_BASE, br.com.seuprojeto.consents.rules.v2.PermissionCatalog.CREDIT_CARD_BILLS);
    }

    private void validateNoMixedPhases(List<String> permissions) {
        boolean hasPhase2 = permissions.stream().anyMatch(br.com.seuprojeto.consents.rules.v2.PermissionCatalog.DATA_SHARING_PERMISSIONS::contains);
        boolean hasPhase3 = permissions.stream().anyMatch(br.com.seuprojeto.consents.rules.v2.PermissionCatalog.SERVICE_INITIATION_PERMISSIONS::contains);

        if (hasPhase2 && hasPhase3) {
            throw new PermissionsValidationException("Não é permitido misturar permissões de Fase 2 com Fase 3.");
        }
    }

    private void validateSinglePhase3Group(List<String> permissions) {
        Set<String> groups = permissions.stream()
            .filter(br.com.seuprojeto.consents.rules.v2.PermissionCatalog.SERVICE_INITIATION_PERMISSIONS::contains)
            .map(p -> br.com.seuprojeto.consents.rules.v2.PermissionCatalog.SERVICE_INITIATION_GROUPS.get(p))
            .collect(Collectors.toSet());

        if (groups.size() > 1) {
            throw new PermissionsValidationException("Só é permitido enviar permissões de um único agrupamento da Fase 3.");
        }
    }
}
