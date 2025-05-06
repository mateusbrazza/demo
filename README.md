# demo
 public ConsentInternalResV3 expireConsent(ConsentInternalResV3 consent) {
    log.info("Verificando expiração do consentimento: {}", consent.getId());

    // Regra 1: Expiração antes da autorização
    if (isExpiredForAuthorization(consent)) {
        log.info("Consentimento expirado sem autorização. Rejeitando com CONSENT_EXPIRED.");

        ConsentInternalResV3 consentDenied = reject(DENIED, consent);
        ConsentInternalResV3 consentWithRejection = addRejection(
            CONSENT_EXPIRED,
            ASPSP,
            "EXPIRED_BEFORE_AUTHORISATION",
            consentDenied
        );

        ConsentInternalResV3 consentUpdated = dataProxyClientV3.updatePartialConsent(
            addRequiredFields(consentWithRejection).getId(),
            convertResInternalToReqInternal(addRequiredFields(consentWithRejection))
        );

        return consentUpdated;
    }

    // Regra 2: Expiração após autorização (data limite de compartilhamento)
    if (isExpiredForConsumption(consent)) {
        log.info("Consentimento autorizado expirou para consumo. Revogando com CONSENT_MAX_DATE_REACHED.");

        ConsentInternalResV3 consentRevoked = reject(REVOKED, consent);
        ConsentInternalResV3 consentWithRejection = addRejection(
            CONSENT_MAX_DATE_REACHED,
            ASPSP,
            "EXPIRED_FOR_CONSUMPTION",
            consentRevoked
        );

        ConsentInternalResV3 consentUpdated = dataProxyClientV3.updatePartialConsent(
            addRequiredFields(consentWithRejection).getId(),
            convertResInternalToReqInternal(addRequiredFields(consentWithRejection))
        );

        return consentUpdated;
    }

    // Nenhuma expiração encontrada
    log.info("Consentimento ainda é válido.");
    return consent;
}
