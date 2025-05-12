package br.com.seuprojeto.consents.exceptions.business;

import br.com.seuprojeto.consents.exceptions.core.ApiException;
import org.springframework.http.HttpStatus;

public class MultiplePhase3GroupsException extends ApiException {
    public MultiplePhase3GroupsException() {
        super("MULTIPLE_PHASE3_GROUPS", "Múltiplos agrupamentos de Fase 3",
              HttpStatus.UNPROCESSABLE_ENTITY,
              "Não é permitido enviar permissões de diferentes agrupamentos de Fase 3.");
    }
}