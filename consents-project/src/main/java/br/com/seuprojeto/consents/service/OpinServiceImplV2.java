package br.com.seuprojeto.consents.service;


import br.com.seuprojeto.consents.dto.ConsentCacheObject;
import br.com.seuprojeto.consents.dto.request.OpinExternalRequestV2;
import br.com.seuprojeto.consents.dto.request.OpinInternalRequestV2;
import br.com.seuprojeto.consents.dto.response.OpinExternalResponseV2;
import br.com.seuprojeto.consents.dto.response.OpinInternalResponseV2;
import br.com.seuprojeto.consents.exceptions.auth.AccessDeniedException;
import br.com.seuprojeto.consents.exceptions.business.ConsentNotFoundException;
import br.com.seuprojeto.consents.exceptions.validation.MissingRequiredFieldException;
import br.com.seuprojeto.consents.exceptions.validation.MissingRequiredHeaderException;
import br.com.seuprojeto.consents.integration.clients.dataproxy.v2.DataProxyClientV2;
import br.com.seuprojeto.consents.mappers.OpinMapperV2;
import br.com.seuprojeto.consents.rules.v2.RuleIdempotencyV2;
import br.com.seuprojeto.consents.rules.v2.RulesOpinV2;
import br.com.seuprojeto.consents.utils.LinksUtils;
import br.com.seuprojeto.consents.utils.MetaUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.*;


import static br.com.seuprojeto.consents.mappers.OpinMapperV2.convertResInternalToResExternalDTO;
import static br.com.seuprojeto.consents.utils.ConsentUtils.*;
import static br.com.seuprojeto.consents.utils.JsonUtils.extractClaimFromPayload;
import static br.com.seuprojeto.consents.utils.LoggerUtils.logConsent;
import static br.com.seuprojeto.consents.utils.RulesConstants.*;

@Slf4j
@Service
public final class OpinServiceImplV2 extends RulesOpinV2 implements OpinServiceV2 {



    private static final String TIPO_PESSOA_FISICA = "F";
    private static final String TIPO_PESSOA_JURIDICA = "J";

    private final CacheManager cacheManager;
    private final RuleIdempotencyV2 ruleIdempotency;
//    private final Eq3Client eq3Client;
//    private final DirectoryClient directoryClient;
    private final LinksUtils linksUtils;
    private final OpinRejectionService opinRejectionService;
    private final DataProxyClientV2 dataProxyClientV2;


    private final Map<String, Object> requestInfo = new HashMap<>();

    public OpinServiceImplV2(CacheManager cacheManager,
                             RuleIdempotencyV2 ruleIdempotency,
                             LinksUtils linksUtils,
                             OpinRejectionService opinRejectionService,
                             DataProxyClientV2 dataProxyClientV2) {
        this.cacheManager = cacheManager;
        this.ruleIdempotency = ruleIdempotency;
        this.linksUtils = linksUtils;
        this.opinRejectionService = opinRejectionService;
        this.dataProxyClientV2 = dataProxyClientV2;
    }

    @Override
    public OpinExternalResponseV2 create(String brandOrg, Map<String, String> headers, OpinExternalRequestV2 opinExternalRequestV2, HttpServletRequest request) {
        log.info("init consent creation, consent={}", opinExternalRequestV2);

        extractRequestInfo(opinExternalRequestV2, headers, brandOrg);
        String idempotencyKey = headers.get(X_IDEMPOTENCY_KEY).toString();
        validateConsent(opinExternalRequestV2,headers);

        Cache cache = cacheManager.getCache("consents");
        ConsentCacheObject  cachedObject = cache != null ? cache.get(idempotencyKey, ConsentCacheObject.class) : null;

        if (cachedObject != null) {
            ruleIdempotency.validateIdempotency(idempotencyKey, cachedObject.getOriginalRequest(), opinExternalRequestV2);
        }

        OpinInternalRequestV2 opinInternalRequestV2 = buildInternalContract(opinExternalRequestV2);
        System.out.println(opinInternalRequestV2);
        OpinInternalResponseV2 opinConsentCreated = dataProxyClientV2.createConsent(opinInternalRequestV2);
        logConsent(opinConsentCreated);

        OpinExternalResponseV2 opinExternalResponseV2 = convertResInternalToResExternalDTO(opinConsentCreated);
        opinExternalResponseV2 = buildResponseMetadata(request, opinExternalResponseV2);

        if (cache != null) {
            ConsentCacheObject cacheObject = ConsentCacheObject.builder()
                    .originalRequest(opinExternalRequestV2)
                    .createdConsent(opinConsentCreated)
                    .build();
            cache.put(idempotencyKey, cacheObject);
        }

        return opinExternalResponseV2;
    }

    @Override
    public OpinExternalResponseV2 get(String consentId, Map<String, String> headers, String brandOrg, HttpServletRequest request) {
        log.info("get consent by id");

        extractRequestInfo(headers, brandOrg);
        validateHeaderFapiInteractionId(headers);

        String consentExtracted = extractConsentId(consentId)
                .orElseThrow(() -> new MissingRequiredFieldException("Consent ID não informado ou mal formatado."));

        OpinInternalResponseV2 consentResFromProxy = Optional.ofNullable(dataProxyClientV2.findConsent(consentExtracted))
                .orElseThrow(() -> new ConsentNotFoundException(String.format("Consentimento [%s] não encontrado.", consentExtracted)));

        isReqClientIdSameAsCreation(consentResFromProxy, requestInfo);

        OpinInternalResponseV2 opinRejectionResult = opinRejectionService.expireConsent(consentResFromProxy);
        logConsent(opinRejectionResult);

        return buildConsentResponse(request, opinRejectionResult);
    }

    @Override
    public void delete(String consentId, String brandOrg, Map<String, String> headers, HttpServletRequest request) {
        extractRequestInfo(headers, brandOrg);
        validateHeaderFapiInteractionId(headers);

        String consentExtracted = extractConsentId(consentId)
                .orElseThrow(() -> new MissingRequiredFieldException("Consent ID não informado ou mal formatado."));

        OpinInternalResponseV2 consentResFromProxy = Optional.ofNullable(dataProxyClientV2.findConsent(consentExtracted))
                .orElseThrow(() -> new ConsentNotFoundException(String.format("Consentimento [%s] não encontrado.", consentExtracted)));

        log.info("consent found for deletion: {}", consentResFromProxy);

        isReqClientIdSameAsCreation(consentResFromProxy, requestInfo);

        OpinInternalResponseV2 consentRejected = opinRejectionService.rejectConsent(consentResFromProxy);
        logConsent(consentRejected);

        log.info("consent deleted with success");
    }

    private OpinExternalResponseV2 buildResponseMetadata(HttpServletRequest request, OpinExternalResponseV2 resExternalDTO) {
        resExternalDTO.setMeta(MetaUtils.buildMetaPageableV2(1, 1));
        resExternalDTO.setLinks(linksUtils.buildLinksV2(request, resExternalDTO));
        return resExternalDTO;
    }

    private OpinExternalResponseV2 buildConsentResponse(HttpServletRequest request, OpinInternalResponseV2 consent) {
        log.info("building consent response, consentId={}", consent.getId());
        OpinExternalResponseV2 externalResData = convertResInternalToResExternalDTO(consent);
        return buildResponseMetadata(request, externalResData);
    }

    private OpinInternalRequestV2 buildInternalContract(OpinExternalRequestV2 req) {
//        boolean isPJ = (boolean) req.getIS_PJ();
//        String idEq3FromCpf = extractIdEq3FromIdentification(req.get(CPF_IDENTIFICATION_NUMBER).toString(), false);
//        String idEq3FromCnpj = isPJ ? extractIdEq3FromIdentification(req.get(CNPJ_IDENTIFICATION_NUMBER).toString(), true) : "";
//        DirectoryOAuthClient authClient = extractClientInfo(req.get(REQUEST_CLIENT_ID).toString());

        Map<String, Object> requestInfo = new HashMap<>();
        requestInfo.put(SUBJECT, "idEq3FromCpf");
        requestInfo.put(SUBJECT_PJ, "idEq3FromCnpj");
        requestInfo.put(OAUTH_CLIENT_DATA, "authClient");
        requestInfo.put(REQUEST_CLIENT_ID,"teste-cllient-id");
        requestInfo.put(BRAND_CODE, extractBrandCode("rede-itau"));


        return OpinMapperV2.convertReqExternalToReqInternalDTO(req, requestInfo);
    }


    private void extractRequestInfo(OpinExternalRequestV2 req, Map<String, String> headers, String brandOrg) {
        log.info("extract request info");

        String jwtToken = Optional.ofNullable(headers.get(X_ITAU_AUTHORIZATION_HEADER))
                .orElseGet(() -> headers.get(AUTHORIZATION_HEADER));
        requestInfo.put(X_IDEMPOTENCY_KEY, headers.get(X_FAPI_INTERACTION_ID));
        requestInfo.put(BRAND_CODE, extractBrandCode(brandOrg));
        requestInfo.put(ORGANIZATION, extractOrganization(brandOrg));
        requestInfo.put(IS_PJ, true);
        requestInfo.put(CPF_IDENTIFICATION_NUMBER,"teste");
        requestInfo.put(CNPJ_IDENTIFICATION_NUMBER,"testePJ");
        requestInfo.put(PARENT_ORG_ID, "extractParentOrgId(headers)");
        requestInfo.put(FAPI_INTERACTION_ID, getFapiInteractionId(headers));
        requestInfo.put(REQUEST_CLIENT_ID, extractClaimFromPayload(jwtToken, CLIENT_ID));
    }

    private void extractRequestInfo( Map<String, String> headers, String brandOrg) {
        log.info("extract request info");

        String jwtToken = Optional.ofNullable(headers.get(X_ITAU_AUTHORIZATION_HEADER))
                .orElseGet(() -> headers.get(AUTHORIZATION_HEADER));
        requestInfo.put(X_IDEMPOTENCY_KEY, headers.get(X_FAPI_INTERACTION_ID));
        requestInfo.put(BRAND_CODE, extractBrandCode(brandOrg));
        requestInfo.put(ORGANIZATION, extractOrganization(brandOrg));
        requestInfo.put(IS_PJ, true);
        requestInfo.put(CPF_IDENTIFICATION_NUMBER,"teste");
        requestInfo.put(CNPJ_IDENTIFICATION_NUMBER,"testePJ");
        requestInfo.put(PARENT_ORG_ID, "extractParentOrgId(headers)");
        requestInfo.put(FAPI_INTERACTION_ID, getFapiInteractionId(headers));
        requestInfo.put(REQUEST_CLIENT_ID, extractClaimFromPayload(jwtToken, CLIENT_ID));
    }

    private  Object getFapiInteractionId(Map<String,String> headers){
        return headers.get(X_FAPI_INTERACTION_ID);
    }

    private void validateHeaderFapiInteractionId(Map<String, String> headers) {
        if (!headers.containsKey(X_FAPI_INTERACTION_ID) || headers.get(X_FAPI_INTERACTION_ID).isBlank()) {
            throw new MissingRequiredHeaderException("Missing FAPI interaction ID header");
        }
    }

    private void isReqClientIdSameAsCreation(OpinInternalResponseV2 consentRecovered, Map<String, Object> requestInfo) {
        String clientActual = String.valueOf(requestInfo.get(REQUEST_CLIENT_ID));
        String clientCreation = consentRecovered.getData().getIdentification().getOauthClientId();

        if (!clientActual.equalsIgnoreCase(clientCreation)) {
            log.info("clientIdActual={}, clientIdCreation={}, msg=Client ID mismatch detected", clientActual, clientCreation);
            throw new AccessDeniedException("O clientId do requisitante não corresponde ao clientId que criou o consentimento.");
        }
    }

//    private String extractIdEq3FromIdentification(String identificationNumber, boolean fromCnpj) {
//        String personType = fromCnpj ? TIPO_PESSOA_JURIDICA : TIPO_PESSOA_FISICA;
//        try {
////            Eq3DataResponse dataResponse = eq3Client.searchPersonIdByDocument(identificationNumber, personType);
//            return "dataResponse.getData().getId()";
//        } catch (Exception e) {
//            throw new Eq3IntegrationException(String.format("Cannot search person by %s. An error occurred when requesting internal service.", personType), e);
//        }
//    }
}
