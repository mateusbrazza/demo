package br.com.seuprojeto.consents.service;



import br.com.seuprojeto.consents.dto.request.OpinExternalRequestV2;
import br.com.seuprojeto.consents.dto.response.OpinExternalResponseV2;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

public sealed interface OpinServiceV2 permits OpinServiceImplV2 {

    OpinExternalResponseV2 create(
            String brandOrg,
            Map<String, String> headers,
            OpinExternalRequestV2 req,
            HttpServletRequest request
    );

    void delete(
            String consentId,
            String brandOrg,
            Map<String, String> headers,
            HttpServletRequest request
    );

    OpinExternalResponseV2 get(
            String consentId,
            Map<String, String> headers,
            String brandOrg,
            HttpServletRequest request
    );
}
