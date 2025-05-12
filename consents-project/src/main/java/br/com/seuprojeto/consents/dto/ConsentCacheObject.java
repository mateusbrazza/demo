package br.com.seuprojeto.consents.dto;

import br.com.seuprojeto.consents.dto.request.OpinExternalRequestV2;
import br.com.seuprojeto.consents.dto.response.OpinInternalResponseV2;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConsentCacheObject {
    private OpinExternalRequestV2 originalRequest;
    private OpinInternalResponseV2  createdConsent;


}
