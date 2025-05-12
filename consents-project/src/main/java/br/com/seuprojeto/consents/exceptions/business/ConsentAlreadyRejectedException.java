package br.com.seuprojeto.consents.exceptions.business;

import br.com.seuprojeto.consents.exceptions.core.ApiException;
import org.springframework.http.HttpStatus;

public class ConsentAlreadyRejectedException extends ApiException {
    public ConsentAlreadyRejectedException(String consentId) {
        super("CONSENT_ALREADY_REJECTED", "Consentimento já rejeitado ou revogado",
              HttpStatus.UNPROCESSABLE_ENTITY,
              "O consentimento com ID '" + consentId + "' já foi rejeitado ou revogado.");
    }
}