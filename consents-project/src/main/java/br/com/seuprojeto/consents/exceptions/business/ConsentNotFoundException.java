package br.com.seuprojeto.consents.exceptions.business;


import br.com.seuprojeto.consents.exceptions.core.ApiException;
import org.springframework.http.HttpStatus;

public class ConsentNotFoundException extends ApiException {
    public ConsentNotFoundException(String detail) {
        super(
                "CONSENT_NOT_FOUND",
                "Consentimento Não Encontrado",
                HttpStatus.NOT_FOUND,
                detail
        );
    }
}
