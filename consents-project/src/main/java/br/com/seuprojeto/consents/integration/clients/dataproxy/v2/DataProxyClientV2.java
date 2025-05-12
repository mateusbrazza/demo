package br.com.seuprojeto.consents.integration.clients.dataproxy.v2;

import br.com.seuprojeto.consents.dto.request.OpinInternalRequestV2;
import br.com.seuprojeto.consents.dto.response.OpinInternalResponseV2;

import br.com.seuprojeto.consents.exceptions.business.ConsentNotFoundException;
import br.com.seuprojeto.consents.exceptions.integration.DataProxyIntegrationException;
import br.com.seuprojeto.consents.rules.v2.RuleIdempotencyV2;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static br.com.seuprojeto.consents.utils.DateUtils.DATE_TIME_FORMAT;

@Slf4j
@Service
public class DataProxyClientV2 {

    private static final String CACHE_NAME = "consents";

    private final CacheManager cacheManager;
    private final RuleIdempotencyV2 ruleIdempotency;

    public DataProxyClientV2(CacheManager cacheManager, RuleIdempotencyV2 ruleIdempotency) {
        this.cacheManager = cacheManager;
        this.ruleIdempotency = ruleIdempotency;
    }

    public OpinInternalResponseV2 findConsent(String consentId) {
        OpinInternalRequestV2 response = getFromCache(consentId);

        if (response == null) {
            throw new ConsentNotFoundException("Consentimento com ID '" + consentId + "' não encontrado.");
        }
        return mapToResponse(response);
    }

    public OpinInternalResponseV2 updatePartialConsent(String consentId, OpinInternalRequestV2 request) {
        try {
            OpinInternalRequestV2 cached = getFromCache(consentId);
            if (cached == null) {
                throw new ConsentNotFoundException("Consentimento com ID '" + consentId + "' não encontrado.");
            }

            if (request.getStatus() != null) {
                cached.setStatus(request.getStatus());
            }

            if (request.getData() != null && request.getData().getRejection() != null) {
                cached.getData().setRejection(request.getData().getRejection());
            }
            String formattedNow = LocalDateTime.now(ZoneOffset.UTC).format(DATE_TIME_FORMAT);
            cached.setUpdatedDate(formattedNow);

            return mapToResponse(cached);
        } catch (ConsentNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Erro ao atualizar consentimento ID '{}'", consentId, ex);
            throw new DataProxyIntegrationException("Erro ao atualizar o consentimento no cache.");
        }
    }

    public OpinInternalResponseV2 createConsent(OpinInternalRequestV2 request) {
        return persistAndReturnConsent(request);
    }

    private OpinInternalResponseV2 persistAndReturnConsent(OpinInternalRequestV2 request) {
        try {
            request.setId(UUID.randomUUID().toString());
            Cache cache = cacheManager.getCache(CACHE_NAME);
            if (cache == null) {
                throw new DataProxyIntegrationException("Cache 'consents' não disponível.");
            }

            cache.put(request.getId(), request);
            OpinInternalRequestV2 cached = cache.get(request.getId(), OpinInternalRequestV2.class);

            if (cached == null) {
                throw new DataProxyIntegrationException("Consentimento salvo no cache está nulo.");
            }

            return mapToResponse(cached);
        } catch (DataProxyIntegrationException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Erro inesperado ao criar consentimento", ex);
            throw new DataProxyIntegrationException("Erro inesperado ao criar consentimento.");
        }
    }

    private OpinInternalRequestV2 getFromCache(String consentId) {
        Cache cache = cacheManager.getCache(CACHE_NAME);
        return cache != null ? cache.get(consentId, OpinInternalRequestV2.class) : null;
    }

    private OpinInternalResponseV2 mapToResponse(OpinInternalRequestV2 request) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setSkipNullEnabled(true);
        return mapper.map(request, OpinInternalResponseV2.class);
    }
}
