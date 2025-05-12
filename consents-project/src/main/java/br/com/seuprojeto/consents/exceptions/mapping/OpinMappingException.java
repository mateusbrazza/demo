package br.com.seuprojeto.consents.exceptions.mapping;

import br.com.seuprojeto.consents.exceptions.core.ApiException;
import org.springframework.http.HttpStatus;

public class OpinMappingException extends ApiException {
    private static final String CODE = "OPIN_MAPPING_ERROR";
    private static final String TITLE = "Erro ao mapear dados internos e externos";

    public OpinMappingException(String detail) {
        super(CODE, TITLE, HttpStatus.INTERNAL_SERVER_ERROR, detail);
    }
}