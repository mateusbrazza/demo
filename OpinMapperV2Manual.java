
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static br.com.seuprojeto.consents.rules.v2.ConsentAdditionalStatusEnum.AWAITING_AUTHORISATION;
import static br.com.seuprojeto.consents.rules.v2.ConsentStatusDirectoryEnum.PENDING;
import static br.com.seuprojeto.consents.utils.ConsentUtils.buildConsentDefinitionV2;
import static br.com.seuprojeto.consents.utils.RulesConstants.*;

public class OpinMapperV2Manual {

    private static final Logger log = LoggerFactory.getLogger(OpinMapperV2Manual.class);
    private static final String VERSION = "2.0";
    private static final String URN_FORMAT = "urn:%s:%s";

    public static OpinExternalResponseV2 toExternalResponse(OpinInternalResponseV2 source) {
        try {
            OpinExternalResponseV2 target = new OpinExternalResponseV2();
            var data = new OpinExternalResponseV2.Data();
            data.setConsentId(convertConsentId(source.getId(), source.getData().getIdentification().getBrandCode()));
            data.setCreationDateTime(source.getCreatedDate());
            data.setStatusUpdateDateTime(source.getUpdatedDate());

            String expiration = source.getData().getExpirationDateTime();
            data.setExpirationDateTime(INDEFINITE_EXPIRATION_DATE.equals(expiration) ? null : expiration);

            var status = convertConsentStatus(source.getStatus());
            data.setStatus(status != null ? status.getDescription() : null);
            target.setData(data);
            return target;
        } catch (Exception ex) {
            log.error("Erro ao converter para response externo", ex);
            throw new OpinMappingException("Erro ao converter response internal para externo.");
        }
    }

    public static OpinInternalRequestV2 toInternalRequest(OpinExternalRequestV2 source, Map<String, Object> requestInfo) {
        try {
            OpinInternalRequestV2 target = new OpinInternalRequestV2();
            target.setActor(source.getData().getLoggedUser().getDocument().getIdentification());
            setDestinationProperties(target, requestInfo);

            boolean isPJ = false;
            String consentSubject = isPJ ? requestInfo.get(SUBJECT_PJ).toString() : requestInfo.get(SUBJECT).toString();
            target.setSubject(consentSubject);
            target.getData().setIdentification(buildIdentificationInternalDTO(requestInfo, isPJ));
            target.getData().setVersion(VERSION);

            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
            target.setCreatedDate(now);
            target.setUpdatedDate(now);

            return target;
        } catch (Exception ex) {
            log.error("Erro ao converter request externo para interno", ex);
            throw new OpinMappingException("Erro ao converter request externo.");
        }
    }

    private static void setDestinationProperties(OpinInternalRequestV2 destination, Map<String, Object> requestInfo) {
        destination.setAudience(requestInfo.get(REQUEST_CLIENT_ID).toString());
        destination.setStatus(PENDING.getInternalDescription());
        destination.getData().setAdditionalStatus(AWAITING_AUTHORISATION.name());
        destination.getData().setPhase3(isPhase3(destination.getData().getPermissions()));
        destination.setDefinition(buildConsentDefinitionV2());
    }

    private static Identification buildIdentificationInternalDTO(Map<String, Object> requestInfo, boolean isPJ) {
        Identification id = new Identification();
        String subject = isPJ ? requestInfo.get(SUBJECT_PJ).toString() : requestInfo.get(SUBJECT).toString();
        String brandCode = requestInfo.get(BRAND_CODE).toString();
        OrganisationEnum organisation = (OrganisationEnum) requestInfo.get(ORGANIZATION);

        BrandEnum brand = BrandEnum.getByBrandCode(brandCode);

        id.setSubject(subject);
        id.setOauthClientId(requestInfo.get(REQUEST_CLIENT_ID).toString());
        id.setOauthClientName("teste");
        id.setBrandCode(brand.getItauBrandCode());
        id.setBrandName(brand.getItauBrandName());
        id.setBrandId(brand.getItauBrandId());
        id.setSoftwareId("software_id");
        id.setOrgName("org_name");
        id.setOrgId("org_id");
        id.setServerOrgId("organisation.getOrgId()");

        return id;
    }

    private static String convertConsentId(String id, String brandCode) {
        return String.format(URN_FORMAT, brandCode, id);
    }

    private static ConsentStatusDirectoryEnum convertConsentStatus(String status) {
        return EnumSet.allOf(ConsentStatusDirectoryEnum.class).stream()
                .filter(s -> s.name().equalsIgnoreCase(status))
                .findAny().orElse(null);
    }

    private static boolean isPhase3(List<String> permissions) {
        return permissions != null && permissions.stream().anyMatch(PermissionEnum.getFase3Permissions()::contains);
    }
}
