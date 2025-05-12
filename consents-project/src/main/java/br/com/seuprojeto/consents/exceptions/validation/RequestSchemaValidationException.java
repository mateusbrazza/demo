package br.com.seuprojeto.consents.exceptions.validation;


import br.com.seuprojeto.consents.exceptions.core.ApiException;
import com.networknt.schema.ValidationMessage;
import org.springframework.http.HttpStatus;

import java.util.Set;
import java.util.stream.Collectors;

public class RequestSchemaValidationException extends ApiException {

    public RequestSchemaValidationException(Set<ValidationMessage> errors) {
        super(
                "INVALID_REQUEST_SCHEMA",
                "Requisição inválida de acordo com o schema JSON",
                HttpStatus.BAD_REQUEST,
                errors.stream().map(ValidationMessage::getMessage).collect(Collectors.joining("; "))
        );
    }
}
