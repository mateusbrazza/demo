package br.com.seuprojeto.consents.rules.v2;


import br.com.seuprojeto.consents.dto.request.OpinExternalRequestV2;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class RuleIdempotencyV2 {

    private static final int IDEMPOTENCY_KEY_MIN_LENGTH = 1;
    private static final int IDEMPOTENCY_KEY_MAX_LENGTH = 255;
    private static final String IDEMPOTENCY_KEY_REGEX = "^[a-zA-Z0-9\\-_.]{1,255}$";

    public void validateIdempotency(String idempotencyKey, OpinExternalRequestV2 req1, OpinExternalRequestV2 req2) {
        validateIdempotencyKey(idempotencyKey);
        if (!isSameRequest(req1, req2)) {
            throw new IllegalArgumentException("Requests are not identical for idempotency validation.");
        }
    }

    private void validateIdempotencyKey(String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("The header idempotency-key is required.");
        }

        if (idempotencyKey.length() < IDEMPOTENCY_KEY_MIN_LENGTH || idempotencyKey.length() > IDEMPOTENCY_KEY_MAX_LENGTH) {
            throw new IllegalArgumentException("IDEMPOTENCY_KEY_INVALID: Length must be between " +
                    IDEMPOTENCY_KEY_MIN_LENGTH + " and " + IDEMPOTENCY_KEY_MAX_LENGTH);
        }

        if (!idempotencyKey.matches(IDEMPOTENCY_KEY_REGEX)) {
            throw new IllegalArgumentException("IDEMPOTENCY_KEY_INVALID: Must match the pattern " + IDEMPOTENCY_KEY_REGEX);
        }
    }

    private boolean isSameRequest(OpinExternalRequestV2 req1, OpinExternalRequestV2 req2) {
        if (req1 == null || req2 == null ||
                req1.getData() == null || req2.getData() == null ||
                req1.getData().getLoggedUser() == null || req2.getData().getLoggedUser() == null ||
                req1.getData().getLoggedUser().getDocument() == null || req2.getData().getLoggedUser().getDocument() == null) {
            return false;
        }

        return Objects.equals(req1.getData().getPermissions(), req2.getData().getPermissions()) &&
                Objects.equals(req1.getData().getExpirationDateTime(), req2.getData().getExpirationDateTime()) &&
                Objects.equals(req1.getData().getLoggedUser().getDocument().getIdentification(), req2.getData().getLoggedUser().getDocument().getIdentification()) &&
                Objects.equals(req1.getData().getLoggedUser().getDocument().getRel(), req2.getData().getLoggedUser().getDocument().getRel());
    }
}
