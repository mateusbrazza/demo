package br.com.seuprojeto.consents.rules.v2;


import br.com.seuprojeto.consents.exceptions.validation.MissingRequiredHeaderException;

import java.util.Map;

import static br.com.seuprojeto.consents.utils.RulesConstants.X_FAPI_INTERACTION_ID;

public class RulesCommon {

    public static void validateHeaderFapiInteractionId(Map<String, String> headers) {
        String fapiInteractionId = headers.get(X_FAPI_INTERACTION_ID);
        if (fapiInteractionId == null || fapiInteractionId.isBlank()) {
            throw new MissingRequiredHeaderException("O header x-fapi-interaction-id é obrigatório.");
        }
    }
}
