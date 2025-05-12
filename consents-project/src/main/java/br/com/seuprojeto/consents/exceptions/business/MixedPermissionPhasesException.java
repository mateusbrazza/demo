package br.com.seuprojeto.consents.exceptions.business;

import br.com.seuprojeto.consents.exceptions.core.ApiException;
import org.springframework.http.HttpStatus;

public class MixedPermissionPhasesException extends ApiException {
    public MixedPermissionPhasesException() {
        super("MIXED_PHASE_PERMISSIONS", "Mistura de permissões Fase 2 e Fase 3",
              HttpStatus.UNPROCESSABLE_ENTITY,
              "Não é permitido misturar permissões de Fase 2 com permissões de Fase 3.");
    }
}