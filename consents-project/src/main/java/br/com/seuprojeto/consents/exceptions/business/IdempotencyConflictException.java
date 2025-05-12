package br.com.seuprojeto.consents.exceptions.business;

import br.com.seuprojeto.consents.exceptions.core.ApiException;
import org.springframework.http.HttpStatus;

public class IdempotencyConflictException extends ApiException {
    public IdempotencyConflictException() {
        super("IDEMPOTENCY_CONFLICT", "Conflito de idempotência",
              HttpStatus.UNPROCESSABLE_ENTITY,
              "O payload enviado com a mesma chave de idempotência é diferente do anterior.");
    }
}