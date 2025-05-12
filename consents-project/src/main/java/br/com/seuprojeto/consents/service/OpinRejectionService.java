package br.com.seuprojeto.consents.service;

import br.com.seuprojeto.consents.dto.rejection.RejectReasonInternalV2;
import br.com.seuprojeto.consents.dto.rejection.RejectionInternalV2;
import br.com.seuprojeto.consents.dto.response.OpinInternalResponseV2;
import br.com.seuprojeto.consents.enums.EnumReasonCode;
import br.com.seuprojeto.consents.enums.EnumRejected;
import br.com.seuprojeto.consents.exceptions.business.ConsentAlreadyRejectedException;
import br.com.seuprojeto.consents.integration.clients.dataproxy.v2.DataProxyClientV2;
import br.com.seuprojeto.consents.rules.v2.ConsentAdditionalStatusEnum;
import br.com.seuprojeto.consents.rules.v2.ConsentStatusDirectoryEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static br.com.seuprojeto.consents.constants.ErrorMessages.CONSENT_ALREADY_REJECTED_MSG;
import static br.com.seuprojeto.consents.enums.EnumReasonCode.*;
import static br.com.seuprojeto.consents.enums.EnumRejected.ASPSP;
import static br.com.seuprojeto.consents.enums.EnumRejected.USER;
import static br.com.seuprojeto.consents.mappers.OpinMapperV2.convertResInternalToReqInternal;
import static br.com.seuprojeto.consents.utils.DateUtils.convertToLocalDateTime;
import static br.com.seuprojeto.consents.utils.RulesConstants.*;

@Slf4j
@Service
public class OpinRejectionService {

    private static final int SECONDS = 3600;

    private final DataProxyClientV2 dataProxyClientV2;

    public OpinRejectionService(DataProxyClientV2 dataProxyClientV2) {
        this.dataProxyClientV2 = dataProxyClientV2;
    }

    public OpinInternalResponseV2 rejectConsent(OpinInternalResponseV2 consent) {
        log.info("rejecting consent: {}", consent.getId());
        if (isPending(consent)) {
            log.info("Consent is pending, rejecting consent: {}", consent.getId());
            OpinInternalResponseV2 consentDenied = reject(ConsentStatusDirectoryEnum.DENIED, consent);
            OpinInternalResponseV2 consentWithRejection = addRejection(
                    CUSTOMER_MANUALLY_REJECTED,
                    USER,
                    REJECTED_BY_USER_INFO,
                    consentDenied
            );
            OpinInternalResponseV2 consentWithRequiredFields = addRequiredFields(consentWithRejection);
            return dataProxyClientV2.updatePartialConsent(
                    consentWithRequiredFields.getId(),
                    convertResInternalToReqInternal(addRequiredFields(consentWithRejection))
            );
        } else if (isAccepted(consent)) {
            log.info("Consent is accepted, revoking consent: {}", consent.getId());
            OpinInternalResponseV2 consentRevoked = reject(ConsentStatusDirectoryEnum.REVOKED, consent);
            OpinInternalResponseV2 consentWithRejection = addRejection(
                    CUSTOMER_MANUALLY_REVOKED,
                    USER,
                    REJECTED_BY_USER_INFO,
                    consentRevoked
            );
            OpinInternalResponseV2 consentWithRequiredFields = addRequiredFields(consentWithRejection);
            return dataProxyClientV2.updatePartialConsent(
                    consentWithRequiredFields.getId(),
                    convertResInternalToReqInternal(consentWithRequiredFields)
            );
        }
        log.info("Accepted consent was rejected");
        throw new ConsentAlreadyRejectedException(String.format(CONSENT_ALREADY_REJECTED_MSG, consent.getId()));
    }

    public OpinInternalResponseV2 expireConsent(OpinInternalResponseV2 consent) {
        log.info("Verificando expiração do consentimento: {}", consent.getId());

        if (isExpiredForAuthorization(consent)) {
            log.info("Consentimento expirado sem autorização. Rejeitando com CONSENT_EXPIRED.");
            OpinInternalResponseV2 consentDenied = reject(ConsentStatusDirectoryEnum.DENIED, consent);
            OpinInternalResponseV2 consentWithRejection = addRejection(
                  CONSENT_EXPIRED,
                    ASPSP,
                   EXPIRED_BEFORE_AUTHORISATION,
                    consentDenied
            );
            OpinInternalResponseV2 consentUpdated = dataProxyClientV2.updatePartialConsent(
                    addRequiredFields(consentWithRejection).getId(),
                    convertResInternalToReqInternal(addRequiredFields(consentWithRejection))
            );
            return consentUpdated;
        }

        if (isExpiredForConsumption(consent)) {
            log.info("Consentimento autorizado expirou para consumo. Revogando com CONSENT_MAX_DATE_REACHED.");
            OpinInternalResponseV2 consentRevoked = reject(ConsentStatusDirectoryEnum.REVOKED, consent);
            OpinInternalResponseV2 consentWithRejection = addRejection(
                    CONSENT_MAX_DATE_REACHED,
                    ASPSP,
                    EXPIRED_FOR_CONSUMPTION,
                    consentRevoked
            );
            OpinInternalResponseV2 consentUpdated = dataProxyClientV2.updatePartialConsent(
                    addRequiredFields(consentWithRejection).getId(),
                    convertResInternalToReqInternal(addRequiredFields(consentWithRejection))
            );
            return consentUpdated;
        }

        log.info("Consentimento ainda é válido");
        return consent;
    }

    public static boolean isExpiredForConsumption(OpinInternalResponseV2 opinInternalResponseV2) {
        log.info("checking if consent is expired for consumption, consentId: {}", opinInternalResponseV2.getId());
        LocalDateTime expirationDateTimeConverted = convertToLocalDateTime(
                opinInternalResponseV2.getData().getExpirationDateTime()
        );
        String additionalStatus = opinInternalResponseV2.getData().getAdditionalStatus();
        String additionalStatusDescription = ConsentAdditionalStatusEnum.AUTHORISED.getDescription();

        boolean isExpired = LocalDateTime.now().isAfter(expirationDateTimeConverted)
                && additionalStatusDescription.equals(additionalStatus);

        log.info("reject msg=Consent is expired for consumption, consentId: {}, isExpired: {}",
                opinInternalResponseV2.getId(), isExpired);
        return isExpired;
    }

    public static boolean isExpiredForAuthorization(OpinInternalResponseV2 opinInternalResponseV2) {
        log.info("checking if consent is expired for authorization, consentId: {}", opinInternalResponseV2.getId());

        LocalDateTime createDateTime = convertToLocalDateTime(opinInternalResponseV2.getCreatedDate());
        LocalDateTime expirationDateTime = convertToLocalDateTime(opinInternalResponseV2.getData().getExpirationDateTime());
        String expectedStatus = ConsentAdditionalStatusEnum.AWAITING_AUTHORISATION.getDescription();
        String consentAdditionalStatus = opinInternalResponseV2.getData().getAdditionalStatus();

        boolean isExpiredByFixedWindow = LocalDateTime.now().isAfter(createDateTime.plusSeconds(SECONDS));
        boolean isExpiredByExplicitExpiration = LocalDateTime.now().isAfter(expirationDateTime);

        log.info("reject msg=Consent is expired for authorization, consentId: {}",
                opinInternalResponseV2.getId());

        return (isExpiredByFixedWindow || isExpiredByExplicitExpiration)
                && expectedStatus.equals(consentAdditionalStatus);
    }

    private static boolean isAccepted(OpinInternalResponseV2 opinInternalResponseV2) {
        log.info("checking if consent is accepted, consentId: {}", opinInternalResponseV2.getId());
        return ConsentStatusDirectoryEnum.ACCEPTED.getInternalDescription()
                .equalsIgnoreCase(opinInternalResponseV2.getStatus());
    }

    private static boolean isPending(OpinInternalResponseV2 opinInternalResponseV2) {
        log.info("checking if consent is pending, consentId: {}", opinInternalResponseV2.getId());
        return ConsentStatusDirectoryEnum.PENDING.getInternalDescription()
                .equalsIgnoreCase(opinInternalResponseV2.getStatus());
    }

    private static OpinInternalResponseV2 reject(
            ConsentStatusDirectoryEnum consentStatus,
            OpinInternalResponseV2 opinInternalResponseV2
    ) {
        log.info("rejecting consentId: {}", opinInternalResponseV2.getId());
        opinInternalResponseV2.setStatus(consentStatus.getInternalDescription());
        opinInternalResponseV2.getData().setAdditionalStatus(consentStatus.getDescription());
        return opinInternalResponseV2;
    }

    private static OpinInternalResponseV2 addRequiredFields(OpinInternalResponseV2 opinInternalResponseV2) {
        log.info("adding required fields to consentId: {}", opinInternalResponseV2.getId());
        opinInternalResponseV2.setUpdatedDate(LocalDateTime.now().format(DATE_TIME_FORMAT));
        return opinInternalResponseV2;
    }

    private static OpinInternalResponseV2 addRejection(
            EnumReasonCode enumReasonCode,
            EnumRejected rejectedBy,
            String additionalInformation,
            OpinInternalResponseV2 opinInternalResponseV2
    ) {
        log.info("adding rejection to consentId: {}", opinInternalResponseV2.getId());

        RejectReasonInternalV2 rejectReasonInternalV2 =new RejectReasonInternalV2(enumReasonCode,additionalInformation);
        RejectionInternalV2 rejectionInternalV2 = new RejectionInternalV2(rejectedBy,rejectReasonInternalV2);
        opinInternalResponseV2.getData().setRejection(rejectionInternalV2);

        return opinInternalResponseV2;
    }
}
