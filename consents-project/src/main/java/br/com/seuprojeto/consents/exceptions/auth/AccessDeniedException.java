package br.com.seuprojeto.consents.exceptions.auth;

import br.com.seuprojeto.consents.exceptions.core.ApiException;
import org.springframework.http.HttpStatus;

public class AccessDeniedException extends ApiException {
    public AccessDeniedException(String detail) {
        super("ACCESS_DENIED", "Acesso Negado", HttpStatus.FORBIDDEN, detail);
    }
}