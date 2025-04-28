package br.com.seuprojeto.consents.service;

import br.com.seuprojeto.consents.dto.request.CreateConsentRequest;
import br.com.seuprojeto.consents.dto.response.ConsentDataResponse;
import br.com.seuprojeto.consents.dto.response.ConsentResponse;
import br.com.seuprojeto.consents.dto.response.LinksResponse;
import br.com.seuprojeto.consents.dto.response.MetaResponse;
import br.com.seuprojeto.consents.exception.InvalidConsentException;
import br.com.seuprojeto.consents.model.ConsentCacheObject;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConsentService {

    private final CacheManager cacheManager;

    public ConsentResponse createConsent(
            String authorization,
            String idempotencyKey,
            String fapiInteractionId,
            String fapiAuthDate,
            String customerIpAddress,
            String customerUserAgent,
            String xV,
            String xMinV,
            CreateConsentRequest request
    ) {
        Cache cache = cacheManager.getCache("consents");
        ConsentCacheObject cachedObject = cache != null ? cache.get(idempotencyKey, ConsentCacheObject.class) : null;

        if (cachedObject != null) {
            if (isSameRequest(cachedObject.getOriginalRequest(), request)) {
                return cachedObject.getConsentResponse();
            } else {
                throw new InvalidConsentException(
                        "IDEMPOTENCY_CONFLICT",
                        "Conflict with existing request",
                        "The request with this idempotency key conflicts with a previous request."
                );
            }
        }

        // Agora atual, truncado para segundos
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS);

        // Parse do expirationDateTime recebido
        OffsetDateTime expirationDateTime = OffsetDateTime.parse(request.getData().getExpirationDateTime());

        // Gerar consentId como URN
        String consentId = "urn:seuprojeto:" + UUID.randomUUID();

        ConsentDataResponse dataResponse = ConsentDataResponse.builder()
                .consentId(consentId)
                .creationDateTime(now)
                .status("AWAITING_AUTHORISATION")
                .statusUpdateDateTime(now)
                .permissions(request.getData().getPermissions())
                .expirationDateTime(expirationDateTime)
                .build();

        LinksResponse linksResponse = LinksResponse.builder()
                .self("/consents/" + consentId)
                .build();

        MetaResponse metaResponse = MetaResponse.builder()
                .totalRecords(1)
                .totalPages(1)
                .build();

        ConsentResponse response = ConsentResponse.builder()
                .data(dataResponse)
                .links(linksResponse)
                .meta(metaResponse)
                .build();

        if (cache != null) {
            ConsentCacheObject newCacheObject = ConsentCacheObject.builder()
                    .consentResponse(response)
                    .originalRequest(request)
                    .build();
            cache.put(idempotencyKey, newCacheObject);
        }
        cache.put(consentId, response);

        return response;
    }

    private boolean isSameRequest(CreateConsentRequest req1, CreateConsentRequest req2) {
        if (req1 == null || req2 == null ||
                req1.getData() == null || req2.getData() == null ||
                req1.getData().getLoggedUser() == null || req2.getData().getLoggedUser() == null ||
                req1.getData().getLoggedUser().getDocument() == null || req2.getData().getLoggedUser().getDocument() == null) {
            return false;
        }

        return Objects.equals(req1.getData().getPermissions(), req2.getData().getPermissions()) &&
                Objects.equals(req1.getData().getExpirationDateTime(), req2.getData().getExpirationDateTime()) &&
                Objects.equals(req1.getData().getLoggedUser().getDocument().getIdentification(), req2.getData().getLoggedUser().getDocument().getIdentification()) &&
                Objects.equals(req1.getData().getLoggedUser().getDocument().getRel(), req2.getData().getLoggedUser().getDocument().getRel());
    }
}
