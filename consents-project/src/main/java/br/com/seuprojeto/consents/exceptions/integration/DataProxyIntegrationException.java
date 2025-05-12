package br.com.seuprojeto.consents.exceptions.integration;

import br.com.seuprojeto.consents.exceptions.core.ApiException;
import org.springframework.http.HttpStatus;

public class DataProxyIntegrationException extends ApiException {
    public DataProxyIntegrationException(String detail) {
        super(
                "DATA_PROXY_INTEGRATION_ERROR",
                "Erro na integração com o Data Proxy",
                HttpStatus.SERVICE_UNAVAILABLE,
                detail
        );
    }
}
