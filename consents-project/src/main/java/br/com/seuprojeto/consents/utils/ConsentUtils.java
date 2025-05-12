package br.com.seuprojeto.consents.utils;



import br.com.seuprojeto.consents.dto.request.*;
import br.com.seuprojeto.consents.exceptions.integration.TokenClaimExtractionException;
import br.com.seuprojeto.consents.rules.v2.organization.OrganisationEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ConsentUtils {

    private static final Logger log = LoggerFactory.getLogger(ConsentUtils.class);

    private ConsentUtils() {}

    private static final String DEFINITION_ID = "openinsurance";
    private static final String DEFINITION_LOCALE = "pt-BR";
    private static final String DEFINITION_VERSION = "1.0";
    private static final Pattern CONSENT_ID_PATTERN = Pattern.compile("^(urn:[a-zA*Z0-9][a-zA-Z0-9\\-]{0,31}):([a-zA-Z0-9()+,\\-.:=@;$_!*'%\\/?#]+$)");

    public static Optional<String> extractConsentId(String param) {
        Matcher matcher = CONSENT_ID_PATTERN.matcher(param);
        return matcher.find() ? Optional.of(matcher.group(2)) : Optional.empty();
    }

    public static boolean isPessoaFisica(String rel) {
        return "CPF".equalsIgnoreCase(rel);
    }

    public static boolean isPessoaJuridica(String rel) {
        return "CNPJ".equalsIgnoreCase(rel);
    }

    public static boolean isPermissaoDePessoaFisica(String permission) {
        return permission.contains("PERSONAL");
    }

    public static boolean isPermissaoDePessoaJuridica(String permission) {
        return permission.contains("BUSINESS");
    }

    public static String extractBrandCode(String brandOrg) {
        String brandCode = Optional.ofNullable(brandOrg)
                .map(b -> b.split("-"))
                .filter(parts -> parts.length > 0)
                .map(parts -> parts[0])
                .orElseThrow(() -> new TokenClaimExtractionException("Path inválido: brandCode ausente"));

        log.info("c=ConsentUtils, m=extractBrandCode, msg={}", brandCode);
        return brandCode;
    }
    public static OrganisationEnum extractOrganization(String brandOrg) {
        OrganisationEnum organization = Optional.ofNullable(brandOrg)
                .map(b -> b.split("-"))
                .filter(parts -> parts.length > 1)
                .map(parts -> parts[1])
                .map(OrganisationEnum::getOrgByOrgName)
                .orElseThrow(() -> new TokenClaimExtractionException("Path inválido: organização ausente"));

        log.info("c=ConsentUtils, m=extractOrganization, msg={}", organization);
        return organization;
    }
    public static String extractCpfIdentificationNumber(OpinExternalRequestV2 dto) {
        return Optional.ofNullable(dto.getData().getLoggedUser().getDocument().getIdentification()).orElse("");
    }

    public static String extractCnpjIdentificationNumber(OpinExternalRequestV2 dto) {
        return Optional.ofNullable(dto.getData().getBusinessEntity())
                .map(CreateBusinessEntity::getDocument)
                .map(CreateDocument::getIdentification)
                .orElse("");
    }

    public static boolean isConsentPJ(OpinExternalRequestV2 dto) {
        return Optional.ofNullable(dto)
                .map(OpinExternalRequestV2::getData)
                .map(CreateConsentData::getBusinessEntity)
                .isPresent();
    }

    public static DefinitionInternalV2 buildConsentDefinitionV2() {
        return new DefinitionInternalV2( DEFINITION_ID, DEFINITION_VERSION, DEFINITION_VERSION, DEFINITION_LOCALE);
    }
}
