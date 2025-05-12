package br.com.seuprojeto.consents.utils;


import br.com.seuprojeto.consents.dto.response.OpinInternalResponseV2;
import org.slf4j.MDC;
import java.util.Optional;

public class LoggerUtils {

    private LoggerUtils() {}

    private static final String CONTEXT_CONSENT_STATUS = "context.consent.status";
    private static final String CONTEXT_CONSENT_ID = "context.consent.consentId";
    private static final String CONTEXT_CONSENT_BRAND_NAME = "context.consent.brandName";
    private static final String CONTEXT_CONSENT_ORG_NAME = "context.consent.orgName";

    public static void logConsent(OpinInternalResponseV2 consent) {
        MDC.put(CONTEXT_CONSENT_STATUS, consent.getStatus());
        MDC.put(CONTEXT_CONSENT_ID, consent.getId());

        Optional.ofNullable(consent.getData())
                .map(data -> data.getIdentification())
                .ifPresent(identification -> {
                    MDC.put(CONTEXT_CONSENT_BRAND_NAME, identification.getBrandCode());
                    MDC.put(CONTEXT_CONSENT_ORG_NAME, identification.getOrgName());
                });
    }
}
