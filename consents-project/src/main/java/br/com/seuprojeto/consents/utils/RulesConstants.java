package br.com.seuprojeto.consents.utils;


import java.time.format.DateTimeFormatter;

public class RulesConstants {

    private RulesConstants() {}

    // Request Headers
    public static final String X_IDEMPOTENCY_KEY = "x-idempotency-key";
    public static final String X_FAPI_INTERACTION_ID = "x-fapi-interaction-id";
    public static final String ORG_ID_INTERNAL = "org_id_internal";
    public static final String X_CORRELATION_ID = "x-itau-correlation-id";
    public static final String X_CLIENT_ID = "x-itau-client-id";
    public static final String X_CLIENT_CERTIFICATE = "X-Client-Certificate";
    public static final String AUTHORIZATION_HEADER = "authorization";
    public static final String X_ITAU_AUTHORIZATION_HEADER = "x-itau-authorization";


    public static final String ERROR_DATETIME_CONVERTION = "Formato de data inválido para o campo expirationDateTime: ";
    public static final String EXPIRATION_DATE_BEFORE_TODAY = "A data de expiração deve ser posterior ao momento atual.";
    public static final String EXPIRATION_DATE_GREATER_THAN_ONE_YEAR = "A data de expiração não pode exceder 1 ano a partir da data atual.";

    // Access Token
    public static final String SCOPE = "scope";
    public static final String CLIENT_ID = "client_id";
    public static final String CONSENT_ID = "consent_id";
    public static final String BEARER_PREFIX = "Bearer ";

    // Expiration
    public static final String INDEFINITE_EXPIRATION_DATE = "2300-01-01T00:00:00Z";
    public static final String REQUIRED_STATUS = "accepted";

    // Request Information
    public static final String BRAND_CODE = "brandCode";
    public static final String ORGANIZATION = "organization";
    public static final String IS_PJ = "isPJ";
    public static final String CPF_IDENTIFICATION_NUMBER = "cpfIdentificationNumber";
    public static final String CNPJ_IDENTIFICATION_NUMBER = "cnpjIdentificationNumber";
    public static final String PARENT_ORG_ID = "parentOrgId";
    public static final String FAPI_INTERACTION_ID = "fapiInteractionId";
    public static final String REQUEST_CLIENT_ID = "clientId";
    public static final String SUBJECT = "subject";
    public static final String SUBJECT_PJ = "subjectPJ";
    public static final String OAUTH_CLIENT_DATA = "oAuthClientData";
    public static final String PAGE = "page";
    public static final String PAGE_SIZE = "page-size";

    // Date Format
    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    // Consent rejection reasons
    public static final String REJECTED_BY_USER_INFO = "Consentimento cancelado por ação do usuário.";
    public static final String EXPIRED_BEFORE_AUTHORISATION = "Consentimento expirou antes que o usuário pudesse confirmá-lo.";
    public static final String EXPIRED_FOR_CONSUMPTION = "O prazo de consumo do consentimento expirou.";
}
