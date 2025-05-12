package br.com.seuprojeto.consents.exceptions.business;

import br.com.seuprojeto.consents.exceptions.core.ApiException;
import org.springframework.http.HttpStatus;

public class ConsentExpiredException extends ApiException {
    public ConsentExpiredException() {
        super("CONSENT_EXPIRED", "Consentimento expirado",
              HttpStatus.UNPROCESSABLE_ENTITY,
              "O consentimento expirou antes de ser autorizado ou após o prazo de consumo.");
    }
}