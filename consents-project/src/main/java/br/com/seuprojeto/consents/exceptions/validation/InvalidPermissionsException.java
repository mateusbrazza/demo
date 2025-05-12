package br.com.seuprojeto.consents.exceptions.validation;

import br.com.seuprojeto.consents.exceptions.core.ApiException;
import org.springframework.http.HttpStatus;

public class InvalidPermissionsException extends ApiException {
    public InvalidPermissionsException(String detail) {
        super("INVALID_PERMISSIONS", "Permissões inválidas",
              HttpStatus.UNPROCESSABLE_ENTITY, detail);
    }
}