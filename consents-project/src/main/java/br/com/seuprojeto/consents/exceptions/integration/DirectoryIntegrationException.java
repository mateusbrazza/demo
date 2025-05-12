package br.com.seuprojeto.consents.exceptions.integration;

import br.com.seuprojeto.consents.exceptions.core.ApiException;
import org.springframework.http.HttpStatus;

public class DirectoryIntegrationException extends ApiException {
    public DirectoryIntegrationException(String detail) {
        super("DIRECTORY_INTEGRATION_ERROR", "Erro ao consultar diretório de participantes",
              HttpStatus.SERVICE_UNAVAILABLE, detail);
    }
}