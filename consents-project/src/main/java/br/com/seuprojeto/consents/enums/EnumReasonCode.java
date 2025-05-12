package br.com.seuprojeto.consents.enums;

public enum EnumReasonCode {
    CONSENT_EXPIRED,
    CUSTOMER_MANUALLY_REJECTED,
    CUSTOMER_MANUALLY_REVOKED,
    CONSENT_MAX_DATE_REACHED,
    CONSENT_TECHNICAL_ISSUE,
    INTERNAL_SECURITY_REASON;

    private EnumReasonCode() {
    }
}
