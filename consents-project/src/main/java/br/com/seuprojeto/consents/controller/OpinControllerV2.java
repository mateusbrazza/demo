package br.com.seuprojeto.consents.controller;


import br.com.seuprojeto.consents.dto.request.OpinExternalRequestV2;
import br.com.seuprojeto.consents.dto.response.OpinExternalResponseV2;
import br.com.seuprojeto.consents.rules.v2.RulesOpinV2;
import br.com.seuprojeto.consents.service.OpinServiceV2;
import br.com.seuprojeto.consents.utils.RequestValidator;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static br.com.seuprojeto.consents.utils.RulesConstants.X_FAPI_INTERACTION_ID;


@RestController
@RequestMapping("open-insurance/{brand-org}/consents")
@RequiredArgsConstructor
public class OpinControllerV2 {

    private static final Logger log = LoggerFactory.getLogger(OpinControllerV2.class);
    private static final String CONSENT_SCHEMA_VALIDATION = "/schemavalidator/opin-create-V2.schema.json";

    private final OpinServiceV2 service;
    private final RequestValidator requestValidator;

    @PostMapping
    public ResponseEntity<OpinExternalResponseV2> create(
            @PathVariable("brand-org") String brandOrg,
            @RequestBody OpinExternalRequestV2 consentrequest,
            @RequestHeader Map<String, String> headers,
            HttpServletRequest request) {

        log.info("Creating consent Opin for brandOrg: {}, consent={}", brandOrg, request);

        requestValidator.validatePostConsentRequestBody(consentrequest, CONSENT_SCHEMA_VALIDATION);

        OpinExternalResponseV2 response = service.create(brandOrg, headers, consentrequest, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .header(X_FAPI_INTERACTION_ID, headers.get(X_FAPI_INTERACTION_ID))
                .body(response);
    }

    @DeleteMapping(value = "/{consentId}")
    public ResponseEntity<Void> delete(
            @PathVariable("consentId") String consentId,
            @PathVariable("brand-org") String brandOrg,
            @RequestHeader Map<String, String> headers,
            HttpServletRequest request) {

        log.info("Deleting consentOpin with ID: {} for brandOrg: {}", consentId, brandOrg);
        service.delete(consentId, brandOrg, headers, request);

        return ResponseEntity.noContent()
                .header(X_FAPI_INTERACTION_ID, headers.get(X_FAPI_INTERACTION_ID))
                .build();
    }

    @GetMapping(value = "/{consentId}")
    public ResponseEntity<OpinExternalResponseV2> get(
            @PathVariable("consentId") String consentId,
            @PathVariable("brand-org") String brandOrg,
            @RequestHeader Map<String, String> headers,
            HttpServletRequest request) {

        log.info("Fetching consentOpin with ID: {} for brandOrg: {}", consentId, brandOrg);
        OpinExternalResponseV2 response = service.get(consentId, headers, brandOrg, request);

        return ResponseEntity.status(HttpStatus.OK)
                .header(X_FAPI_INTERACTION_ID, headers.get(X_FAPI_INTERACTION_ID))
                .body(response);
    }
}