package br.com.seuprojeto.consents.controller;

import br.com.seuprojeto.consents.dto.request.CreateConsentRequest;
import br.com.seuprojeto.consents.dto.response.ConsentResponse;
import br.com.seuprojeto.consents.exception.BadRequestException;
import br.com.seuprojeto.consents.exception.UnauthorizedException;
import br.com.seuprojeto.consents.service.ConsentQueryService;
import br.com.seuprojeto.consents.service.ConsentService;
import br.com.seuprojeto.consents.validation.ConsentValidationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/consents")
@RequiredArgsConstructor
public class ConsentController {

    private final ConsentService consentService;
    private final ConsentValidationService consentValidationService;
    private final ConsentQueryService consentQueryService;

    @GetMapping("/{consentId}")
    public ResponseEntity<ConsentResponse> getConsent(
            @PathVariable String consentId,
            @RequestHeader("Authorization") String authorization,
            @RequestHeader("x-fapi-interaction-id") String fapiInteractionId) {

        consentValidationService.validateBasicHeaders(authorization, fapiInteractionId);

        ConsentResponse response = consentQueryService.getConsentById(consentId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{consentId}")
    public ResponseEntity<Void> deleteConsent(
            @PathVariable String consentId,
            @RequestHeader("Authorization") String authorization,
            @RequestHeader("x-fapi-interaction-id") String fapiInteractionId) {

        consentValidationService.validateBasicHeaders(authorization, fapiInteractionId);

        consentQueryService.deleteConsentById(consentId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<ConsentResponse> createConsent(
            @RequestHeader("Authorization") String authorization,
            @RequestHeader("x-idempotency-key") String idempotencyKey,
            @RequestHeader("x-fapi-interaction-id") String fapiInteractionId,
            @RequestHeader(value = "x-fapi-auth-date", required = false) String fapiAuthDate,
            @RequestHeader(value = "x-fapi-customer-ip-address", required = false) String customerIpAddress,
            @RequestHeader(value = "x-customer-user-agent", required = false) String customerUserAgent,
            @RequestHeader(value = "x-v", required = false) String xV,
            @RequestHeader(value = "x-min-v", required = false) String xMinV,
            @Valid @RequestBody CreateConsentRequest request) {

        consentValidationService.validateCreateHeaders(authorization, idempotencyKey, fapiInteractionId);
        consentValidationService.validateCreateConsentRequest(request);

        ConsentResponse response = consentService.createConsent(
                authorization, idempotencyKey, fapiInteractionId,
                fapiAuthDate, customerIpAddress, customerUserAgent,
                xV, xMinV, request);

        return ResponseEntity.status(201).body(response);
    }

}
