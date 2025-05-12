package br.com.seuprojeto.consents.exceptions.validation;

import br.com.seuprojeto.consents.exceptions.core.ApiException;
import org.springframework.http.HttpStatus;

public class MissingRequiredHeaderException extends ApiException {

    private static final String CODE = "MISSING_REQUIRED_HEADER";
    private static final String TITLE = "Cabeçalho obrigatório ausente ou inválido";

    public MissingRequiredHeaderException(String detail) {
        super(CODE, TITLE, HttpStatus.BAD_REQUEST, detail);
    }
}