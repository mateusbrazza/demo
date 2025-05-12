package br.com.seuprojeto.consents.exceptions.validation;

import br.com.seuprojeto.consents.exceptions.core.ApiException;
import org.springframework.http.HttpStatus;

public class InvalidJsonConversionException extends ApiException {

    public InvalidJsonConversionException(String detail) {
        super(
                "INVALID_JSON_CONVERSION",
                "Erro ao converter JSON da requisição",
                HttpStatus.INTERNAL_SERVER_ERROR,
                detail
        );
    }
}
