package br.com.seuprojeto.consents.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public enum PermissionEnum {
    // Fase 2
    RESOURCES_READ,
    CUSTOMERS_PERSONAL_IDENTIFICATIONS_READ,
    CUSTOMERS_PERSONAL_ADRESSES_READ,
    CUSTOMERS_PERSONAL_PHONES_READ,
    CUSTOMERS_PERSONAL_EMAILS_READ,
    CUSTOMERS_PERSONAL_RELATIONSHIPS_READ,
    CUSTOMERS_BUSINESS_IDENTIFICATIONS_READ,
    CUSTOMERS_BUSINESS_ADRESSES_READ,
    CUSTOMERS_BUSINESS_PHONES_READ,
    CUSTOMERS_BUSINESS_EMAILS_READ,
    CUSTOMERS_BUSINESS_RELATIONSHIPS_READ,
    CUSTOMERS_BUSINESS_EMPLOYEES_READ,
    INSURANCES_CARS_READ,
    INSURANCES_HOME_READ,
    INSURANCES_PEOPLE_READ,
    INSURANCES_DENTAL_READ,
    CLAIMS_CARS_READ,
    CLAIMS_HOME_READ,
    CLAIMS_PEOPLE_READ,
    CLAIMS_DENTAL_READ,

    // Fase 3
    QUOTE_AUTO_LEAD_CREATE,
    QUOTE_AUTO_LEAD_UPDATE,
    QUOTE_PATRIMONIAL_LEAD_CREATE,
    QUOTE_PATRIMONIAL_LEAD_UPDATE,
    QUOTE_HOUSING_LEAD_CREATE,
    QUOTE_HOUSING_LEAD_UPDATE,
    CONTRACT_PENSION_PLAN_LEAD_CREATE,
    CONTRACT_PENSION_PLAN_LEAD_UPDATE,
    CONTRACT_LIFE_PENSION_LEAD_CREATE,
    CONTRACT_LIFE_PENSION_LEAD_UPDATE,
    CLAIM_NOTIFICATION_REQUEST_DAMAGE_CREATE,
    CLAIM_NOTIFICATION_REQUEST_PERSON_CREATE,
    ENDORSEMENT_REQUEST_CREATE,
    PENSION_WITHDRAWAL_CREATE,
    PENSION_WITHDRAWAL_LEAD_CREATE,
    CAPITALIZATION_TITLE_WITHDRAWAL_CREATE,
    PERSON_WITHDRAWAL_CREATE;

    public static Set<String> getFase2Permissions() {
        return Set.of(
                RESOURCES_READ.name(),
                CUSTOMERS_PERSONAL_IDENTIFICATIONS_READ.name(),
                CUSTOMERS_PERSONAL_ADRESSES_READ.name(),
                CUSTOMERS_PERSONAL_PHONES_READ.name(),
                CUSTOMERS_PERSONAL_EMAILS_READ.name(),
                CUSTOMERS_PERSONAL_RELATIONSHIPS_READ.name(),
                CUSTOMERS_BUSINESS_IDENTIFICATIONS_READ.name(),
                CUSTOMERS_BUSINESS_ADRESSES_READ.name(),
                CUSTOMERS_BUSINESS_PHONES_READ.name(),
                CUSTOMERS_BUSINESS_EMAILS_READ.name(),
                CUSTOMERS_BUSINESS_RELATIONSHIPS_READ.name(),
                CUSTOMERS_BUSINESS_EMPLOYEES_READ.name(),
                INSURANCES_CARS_READ.name(),
                INSURANCES_HOME_READ.name(),
                INSURANCES_PEOPLE_READ.name(),
                INSURANCES_DENTAL_READ.name(),
                CLAIMS_CARS_READ.name(),
                CLAIMS_HOME_READ.name(),
                CLAIMS_PEOPLE_READ.name(),
                CLAIMS_DENTAL_READ.name()
        );
    }

    public static Set<String> getFase3Permissions() {
        return Set.of(
                QUOTE_AUTO_LEAD_CREATE.name(),
                QUOTE_AUTO_LEAD_UPDATE.name(),
                QUOTE_PATRIMONIAL_LEAD_CREATE.name(),
                QUOTE_PATRIMONIAL_LEAD_UPDATE.name(),
                QUOTE_HOUSING_LEAD_CREATE.name(),
                QUOTE_HOUSING_LEAD_UPDATE.name(),
                CONTRACT_PENSION_PLAN_LEAD_CREATE.name(),
                CONTRACT_PENSION_PLAN_LEAD_UPDATE.name(),
                CONTRACT_LIFE_PENSION_LEAD_CREATE.name(),
                CONTRACT_LIFE_PENSION_LEAD_UPDATE.name(),
                CLAIM_NOTIFICATION_REQUEST_DAMAGE_CREATE.name(),
                CLAIM_NOTIFICATION_REQUEST_PERSON_CREATE.name(),
                ENDORSEMENT_REQUEST_CREATE.name(),
                PENSION_WITHDRAWAL_CREATE.name(),
                PENSION_WITHDRAWAL_LEAD_CREATE.name(),
                CAPITALIZATION_TITLE_WITHDRAWAL_CREATE.name(),
                PERSON_WITHDRAWAL_CREATE.name()
        );
    }

    public static Set<String> getAllPermissions() {
        return Arrays.stream(PermissionEnum.values())
                .map(Enum::name)
                .collect(Collectors.toSet());
    }
}
