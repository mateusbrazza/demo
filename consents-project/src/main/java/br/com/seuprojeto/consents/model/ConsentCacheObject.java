package br.com.seuprojeto.consents.model;

import br.com.seuprojeto.consents.dto.request.CreateConsentRequest;
import br.com.seuprojeto.consents.dto.response.ConsentResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConsentCacheObject {
    private ConsentResponse consentResponse;
    private CreateConsentRequest originalRequest;
}
