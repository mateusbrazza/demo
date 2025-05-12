package br.com.seuprojeto.consents.constants;


public class ErrorMessages {

    // Date validation
    public static final String ERROR_DATETIME_CONVERTION = "Formato de data inválido para o campo expirationDateTime: ";
    public static final String EXPIRATION_DATE_BEFORE_TODAY = "A data de expiração deve ser posterior ao momento atual.";
    public static final String EXPIRATION_DATE_GREATER_THAN_ONE_YEAR = "A data de expiração não pode exceder 1 ano a partir da data atual.";

    // Headers
    public static final String MISSING_AUTHORIZATION = "O header Authorization está ausente ou inválido.";
    public static final String MISSING_FAPI_INTERACTION_ID = "O header x-fapi-interaction-id está ausente ou inválido.";
    public static final String MISSING_IDEMPOTENCY_KEY = "O header x-idempotency-key está ausente ou inválido.";

    public static final String JWT_PAYLOAD_EXTRACTION_ERROR = "Erro ao extrair o payload do token JWT.";
    public static final String JWT_CLAIM_EXTRACTION_ERROR = "Erro ao extrair a claim \"%s\" do payload JWT.";

    public static final String CONSENT_ALREADY_REJECTED_MSG = "Consent [%s] has already been rejected or revoked and cannot be changed.";


    // Permissions
    public static final String PERMISSIONS_EMPTY = "A lista de permissões não pode ser vazia.";
    public static final String INVALID_PERMISSIONS = "Permissões inválidas: ";
    public static final String MIXED_PHASES = "Não pode misturar permissões de Fase 2 e Fase 3.";
    public static final String MISSING_VALID_PHASE = "Nenhuma permissão válida foi fornecida.";
    public static final String RESOURCES_READ_ONLY = "É obrigatório enviar também permissões específicas, além do RESOURCES_READ.";
    public static final String MISSING_RESOURCES_READ = "O consentimento de Fase 2 deve incluir RESOURCES_READ.";
    public static final String MULTIPLE_PHASE3_GROUPS = "Todas as permissões da Fase 3 devem pertencer ao mesmo agrupamento.";
    public static final String MISSING_REQUIRED_PHASE3 = "Fase 3 exige todas as permissões obrigatórias do grupo ";
}
