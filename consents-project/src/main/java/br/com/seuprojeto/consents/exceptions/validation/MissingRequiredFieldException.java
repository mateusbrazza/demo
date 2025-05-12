package br.com.seuprojeto.consents.exceptions.validation;

import br.com.seuprojeto.consents.exceptions.core.ApiException;
import org.springframework.http.HttpStatus;

public class MissingRequiredFieldException extends ApiException {
    public MissingRequiredFieldException(String fieldName) {
        super("MISSING_REQUIRED_FIELD", "Campo obrigatório ausente",
              HttpStatus.BAD_REQUEST,
              "O campo obrigatório '" + fieldName + "' não foi informado.");
    }
}