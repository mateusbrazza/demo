package br.com.seuprojeto.consents.service;

import br.com.seuprojeto.consents.dto.response.ConsentResponse;
import br.com.seuprojeto.consents.exception.ConsentNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConsentQueryService {

    private final CacheManager cacheManager;

    public ConsentResponse getConsentById(String consentId) {
        var cache = cacheManager.getCache("consents");
        ConsentResponse response = cache != null ? cache.get(consentId, ConsentResponse.class) : null;

        if (response == null) {
            throw new ConsentNotFoundException("Consentimento não encontrado: " + consentId);
        }

        return response;
    }

    public void deleteConsentById(String consentId) {
        var cache = cacheManager.getCache("consents");
        if (cache == null || cache.get(consentId) == null) {
            throw new ConsentNotFoundException("Consentimento não encontrado: " + consentId);
        }
        cache.evict(consentId);
    }
}
