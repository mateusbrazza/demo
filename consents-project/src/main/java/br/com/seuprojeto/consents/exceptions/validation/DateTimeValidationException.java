package br.com.seuprojeto.consents.exceptions.validation;

import br.com.seuprojeto.consents.exceptions.core.ApiException;
import org.springframework.http.HttpStatus;

public class DateTimeValidationException extends ApiException {
    public DateTimeValidationException(String detail) {
        super("DATETIME_VALIDATION_ERROR", "Erro de Validação de Data/Hora",
              HttpStatus.BAD_REQUEST, detail);
    }
}