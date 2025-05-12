package br.com.seuprojeto.consents.exceptions.validation;

import br.com.seuprojeto.consents.exceptions.core.ApiException;
import org.springframework.http.HttpStatus;

public class BusinessEntityRequiredException extends ApiException {
    public BusinessEntityRequiredException() {
        super("BUSINESS_ENTITY_REQUIRED", "businessEntity obrigatório",
              HttpStatus.BAD_REQUEST,
              "Para permissões de pessoa jurídica é obrigatório informar o objeto businessEntity.");
    }
}