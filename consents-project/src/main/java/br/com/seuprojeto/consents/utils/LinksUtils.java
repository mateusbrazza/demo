package br.com.seuprojeto.consents.utils;

import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import br.com.seuprojeto.consents.dto.response.OpinExternalResponseV2;
import br.com.seuprojeto.consents.dto.response.LinksExternalResponseV2;

@Component
public class LinksUtils {

    public LinksExternalResponseV2 buildLinksV2(HttpServletRequest request, OpinExternalResponseV2 resExternalDTO) {
        String url = getSelfURL(request, resExternalDTO);
        LinksExternalResponseV2 linksExternalDTO = new LinksExternalResponseV2();
        linksExternalDTO.setSelf(url);
        return linksExternalDTO;
    }

    private String getSelfURL(HttpServletRequest request, OpinExternalResponseV2 resExternalDTO) {
        // TODO pegar do quickconfig
        String host = "https://secure.opf.api.itau";
        String url = host + request.getRequestURI();
        if (!url.contains("urn:")) {
            url = url + "/" + resExternalDTO.getData().getConsentId();
        }
        return url;
    }
}
