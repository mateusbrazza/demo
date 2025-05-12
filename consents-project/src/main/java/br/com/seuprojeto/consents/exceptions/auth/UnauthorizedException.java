package br.com.seuprojeto.consents.exceptions.auth;


import br.com.seuprojeto.consents.exceptions.core.ApiException;
import org.springframework.http.HttpStatus;

public class UnauthorizedException extends ApiException {

    private static final String CODE = "UNAUTHORIZED";
    private static final String TITLE = "Cabeçalho de autorização ausente ou inválido";

    public UnauthorizedException(String detail) {
        super(CODE, TITLE, HttpStatus.UNAUTHORIZED, detail);
    }
}