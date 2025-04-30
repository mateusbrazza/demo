package br.com.seuprojeto.consents.rules;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class PermissionCatalog {

    // Permissões de compartilhamento de dados (Fase 2)
    public static final Set<String> DATA_SHARING_PERMISSIONS = Set.of(
            "CAPITALIZATION_TITLE_EVENTS_READ",
            "CAPITALIZATION_TITLE_PLANINFO_READ",
            "CAPITALIZATION_TITLE_READ",
            "CAPITALIZATION_TITLE_SETTLEMENTS_READ",
            "CUSTOMERS_BUSINESS_ADDITIONALINFO_READ",
            "CUSTOMERS_BUSINESS_IDENTIFICATIONS_READ",
            "CUSTOMERS_PERSONAL_ADDITIONALINFO_READ",
            "CUSTOMERS_PERSONAL_IDENTIFICATIONS_READ",
            "DAMAGES_AND_PEOPLE_AUTO_EVENTS_READ",
            "DAMAGES_AND_PEOPLE_AUTO_PLANINFO_READ",
            "DAMAGES_AND_PEOPLE_AUTO_READ",
            "DAMAGES_AND_PEOPLE_AUTO_SETTLEMENTS_READ",
            "FINANCIAL_ASSISTANCE_EVENTS_READ",
            "FINANCIAL_ASSISTANCE_PLANINFO_READ",
            "FINANCIAL_ASSISTANCE_READ",
            "FINANCIAL_ASSISTANCE_SETTLEMENTS_READ",
            "LIFE_PENSION_EVENTS_READ",
            "LIFE_PENSION_PLANINFO_READ",
            "LIFE_PENSION_READ",
            "LIFE_PENSION_SETTLEMENTS_READ",
            "PENSION_PLAN_READ",
            "RESOURCES_READ"
    );

    // Permissões de iniciação de serviços (Fase 3)
    public static final Map<String, String> SERVICE_INITIATION_GROUPS = Map.ofEntries(
            Map.entry("ENDORSEMENT_REQUEST_CREATE", "ENDORSEMENT"),
            Map.entry("CLAIM_NOTIFICATION_REQUEST_DAMAGE_CREATE", "CLAIM"),
            Map.entry("CLAIM_NOTIFICATION_REQUEST_PERSON_CREATE", "CLAIM"),
            Map.entry("QUOTE_ACCEPTANCE_AND_BRANCHES_ABROAD_LEAD_CREATE", "QUOTE"),
            Map.entry("QUOTE_ACCEPTANCE_AND_BRANCHES_ABROAD_LEAD_UPDATE", "QUOTE"),
            Map.entry("QUOTE_AUTO_CREATE", "QUOTE"),
            Map.entry("QUOTE_AUTO_LEAD_CREATE", "QUOTE"),
            Map.entry("QUOTE_AUTO_LEAD_UPDATE", "QUOTE"),
            Map.entry("CONTRACT_LIFE_PENSION_CREATE", "CONTRACT"),
            Map.entry("CONTRACT_LIFE_PENSION_LEAD_CREATE", "CONTRACT"),
            Map.entry("CONTRACT_LIFE_PENSION_LEAD_PORTABILITY_CREATE", "CONTRACT"),
            Map.entry("CONTRACT_LIFE_PENSION_LEAD_PORTABILITY_UPDATE", "CONTRACT"),
            Map.entry("CONTRACT_LIFE_PENSION_LEAD_UPDATE", "CONTRACT"),
            Map.entry("PENSION_WITHDRAWAL_CREATE", "PENSION"),
            Map.entry("PENSION_WITHDRAWAL_LEAD_CREATE", "PENSION"),
            Map.entry("CAPITALIZATION_TITLE_WITHDRAWAL_CREATE", "CAPITALIZATION"),
            Map.entry("PERSON_WITHDRAWAL_CREATE", "PERSON")
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
