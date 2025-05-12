package br.com.seuprojeto.consents.exceptions.integration;

import br.com.seuprojeto.consents.exceptions.core.ApiException;
import org.springframework.http.HttpStatus;

public class TokenClaimExtractionException extends ApiException {
    private static final String CODE = "TOKEN_CLAIM_EXTRACTION_ERROR";
    private static final String TITLE = "Erro ao extrair claims do token";

    public TokenClaimExtractionException(String detail) {
        super(CODE, TITLE, HttpStatus.UNAUTHORIZED, detail);
    }
}