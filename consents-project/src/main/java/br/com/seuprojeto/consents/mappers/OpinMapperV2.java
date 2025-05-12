package br.com.seuprojeto.consents.mappers;


import br.com.seuprojeto.consents.dto.request.Identification;
import br.com.seuprojeto.consents.dto.request.OpinExternalRequestV2;
import br.com.seuprojeto.consents.dto.request.OpinInternalRequestV2;
import br.com.seuprojeto.consents.dto.response.OpinExternalResponseV2;
import br.com.seuprojeto.consents.dto.response.OpinInternalResponseV2;
import br.com.seuprojeto.consents.enums.PermissionEnum;
import br.com.seuprojeto.consents.exceptions.mapping.OpinMappingException;
import br.com.seuprojeto.consents.rules.v2.ConsentStatusDirectoryEnum;
import br.com.seuprojeto.consents.rules.v2.brand.BrandEnum;
import br.com.seuprojeto.consents.rules.v2.organization.OrganisationEnum;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.modelmapper.Converter;


import static br.com.seuprojeto.consents.rules.v2.ConsentAdditionalStatusEnum.AWAITING_AUTHORISATION;
import static br.com.seuprojeto.consents.rules.v2.ConsentStatusDirectoryEnum.PENDING;
import static br.com.seuprojeto.consents.utils.ConsentUtils.buildConsentDefinitionV2;
import static br.com.seuprojeto.consents.utils.RulesConstants.*;

public class OpinMapperV2 {

    private static final Logger log = LoggerFactory.getLogger(OpinMapperV2.class);
    private static final ModelMapper mapper = new ModelMapper();

    private static final String ORG_NAME_FIELD = "org_name";
    private static final String ORG_ID_FIELD = "org_id";
    private static final String VERSION = "2.0";
    private static final String SOFTWARE_ID_FIELD = "software_id";
    private static final String URN_FORMAT = "urn:%s:%s";

    public static OpinExternalResponseV2 convertResInternalToResExternalDTO(OpinInternalResponseV2 opinInternalResponseV2) {
        try {
            return mapper.typeMap(OpinInternalResponseV2.class, OpinExternalResponseV2.class)
                    .addMapping(OpinInternalResponseV2::getId,(destination, value) -> destination.getData().setConsentId((String)value))
                    .addMapping(OpinInternalResponseV2::getCreatedDate,(destination, value) -> destination.getData().setCreationDateTime((String)value))
                    .addMapping(OpinInternalResponseV2::getUpdatedDate,(destination, value) -> destination.getData().setStatusUpdateDateTime((String)value))
                    .setPostConverter(internalResToExternalResConverter())
                    .map(opinInternalResponseV2);
        } catch (RuntimeException ex) {
            log.error("c=ConsentMapperV2, m=convertResInternalToResExternalDTO, ex=ConsentMappingException", ex);
            throw new OpinMappingException("Error ao converter response internal para response externo");
        }
    }

    public static OpinInternalRequestV2 convertReqExternalToReqInternalDTO(OpinExternalRequestV2 opinConsentExternalRequest, Map<String, Object> requestInfo) {
        try {
            return mapper.typeMap(OpinExternalRequestV2.class, OpinInternalRequestV2.class)
                    .addMappings(mapper -> mapper.map(src -> src.getData().getLoggedUser().getDocument().getIdentification(),
                            (dest, value) -> dest.setActor((String) value)))
                    .setPostConverter(buildRegInternalConverter(requestInfo))
                    .map(opinConsentExternalRequest);
        } catch (RuntimeException ex) {
            log.error("c=OpinMapperV2, m=convertRegExternalToRegInternalDTO, ex=ConsentMappingException", ex);
            throw new OpinMappingException("Error ao converter request extern para requeste interna");
        }
    }

    private static Converter<OpinInternalResponseV2, OpinExternalResponseV2> internalResToExternalResConverter() {
        return context -> {
            OpinInternalResponseV2 source = context.getSource();
            OpinExternalResponseV2 destination = context.getDestination();

            String expirationDateTime = source.getData().getExpirationDateTime();
            if (INDEFINITE_EXPIRATION_DATE.equals(expirationDateTime)) {
                destination.getData().setExpirationDateTime(null);
            } else {
                destination.getData().setExpirationDateTime(expirationDateTime);
            }

            String internalConsentStatus = context.getSource().getStatus();
             destination.getData().setStatus(convertConsentStatus(internalConsentStatus).getDescription());

            String internalConsentId = context.getSource().getId();
            String internalBrandCode = context.getSource().getData().getIdentification().getBrandCode();
            destination.getData().setConsentId(convertConsentId(internalConsentId, internalBrandCode));

            return destination;
        };
    }

    private static String convertConsentId(String consentIdFrom, String internalBrandCode) {
        return String.format(URN_FORMAT, internalBrandCode, consentIdFrom);
    }

    private static ConsentStatusDirectoryEnum convertConsentStatus(String consentFrom) {
        Optional<ConsentStatusDirectoryEnum> optStatus = EnumSet.allOf(ConsentStatusDirectoryEnum.class).stream()
                .filter(cs -> cs.name().equalsIgnoreCase(consentFrom))
                .findAny();
        return optStatus.orElse(null);
    }

    private static Converter<OpinExternalRequestV2, OpinInternalRequestV2> buildRegInternalConverter(Map<String, Object> requestInfo) {
        boolean isPJ = false;
        return context -> {
            OpinInternalRequestV2 destination = context.getDestination();
            setDestinationProperties(destination, requestInfo);

            Identification identificationInternalDTO = buildIdentificationInternalDTO(requestInfo, isPJ);
            String consentSubject = isPJ ? requestInfo.get(SUBJECT_PJ).toString() : requestInfo.get(SUBJECT).toString();

            destination.setSubject(consentSubject);
            destination.getData().setIdentification(identificationInternalDTO);
            destination.getData().setVersion(VERSION);
            destination.setCreatedDate(LocalDateTime.now().format(DATE_TIME_FORMAT));
            destination.setUpdatedDate(LocalDateTime.now().format(DATE_TIME_FORMAT));

            return destination;
        };
    }

    private static void setDestinationProperties(OpinInternalRequestV2 destination, Map<String, Object> requestInfo) {
        destination.setAudience(requestInfo.get(REQUEST_CLIENT_ID).toString());
        destination.setStatus(PENDING.getInternalDescription());
        destination.getData().setAdditionalStatus(AWAITING_AUTHORISATION.name());
        destination.getData().setPhase3(isPhase3(destination.getData().getPermissions()));
        destination.setDefinition(buildConsentDefinitionV2());
    }

    private static Identification buildIdentificationInternalDTO(Map<String, Object> requestInfo, boolean isPJ) {
//        DirectoryOAuthClient requestClient = (DirectoryOAuthClient) requestInfo.get(OAUTH_CLIENT_DATA);

        Identification identificationInternalDTO = new Identification();
        String ide03CPF = requestInfo.get(SUBJECT).toString();
        String oAuthClientId = requestInfo.get(REQUEST_CLIENT_ID).toString();
        String brandCode = requestInfo.get(BRAND_CODE).toString();
        OrganisationEnum organisation = (OrganisationEnum) requestInfo.get(ORGANIZATION);

        String oAuthClientName = "teste";
        String oAuthClientOrgName = extractDataFromSupplementalInfoOAuthClient("requestClient", ORG_NAME_FIELD);
        String oAuthClientOrgId = extractDataFromSupplementalInfoOAuthClient("requestClient", ORG_ID_FIELD);
        String softwareId = extractDataFromSupplementalInfoOAuthClient("requestClient", SOFTWARE_ID_FIELD);

        if (isPJ) {
            String ide03CNPJ = requestInfo.get(SUBJECT_PJ).toString();
            identificationInternalDTO.setSubject(ide03CNPJ);
        } else {
            identificationInternalDTO.setSubject(ide03CPF);
        }

        BrandEnum brand = BrandEnum.getByBrandCode(brandCode);

        identificationInternalDTO.setOauthClientId(oAuthClientId);
        identificationInternalDTO.setOauthClientName(oAuthClientName);
        identificationInternalDTO.setBrandCode(brand.getItauBrandCode());
        identificationInternalDTO.setBrandName(brand.getItauBrandName());
        identificationInternalDTO.setBrandId(brand.getItauBrandId());
        identificationInternalDTO.setSoftwareId(softwareId);
        identificationInternalDTO.setOrgName(oAuthClientOrgName);
        identificationInternalDTO.setOrgId(oAuthClientOrgId);
        identificationInternalDTO.setServerOrgId("organisation.getOrgId()");
        return identificationInternalDTO;
    }

    private static String extractDataFromSupplementalInfoOAuthClient(String client, String fieldName) {
        return "teste";
    }

    private static boolean isPhase3(List<String> permissions) {
        return permissions != null && permissions.stream().anyMatch(PermissionEnum.getFase3Permissions()::contains);
    }

    public static OpinInternalRequestV2 convertResInternalToReqInternal(OpinInternalResponseV2 opinInternalResponseV2) {
        try {
            return mapper.typeMap(OpinInternalResponseV2.class, OpinInternalRequestV2.class)
                    .map(opinInternalResponseV2);
        } catch (RuntimeException ex) {
            log.error("c=OpinMapperV2, m=convertResInternalToRegInternal, ex=ConsentMappingException", ex);
            throw new OpinMappingException("Erro ao converter response internal para request interna ");
        }
    }
}