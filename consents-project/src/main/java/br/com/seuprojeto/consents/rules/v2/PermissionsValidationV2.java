package br.com.seuprojeto.consents.rules.v2;

import br.com.seuprojeto.consents.exceptions.validation.InvalidPermissionsException;
import br.com.seuprojeto.consents.exceptions.validation.MissingRequiredFieldException;

import java.util.List;
import java.util.Set;

public class PermissionsValidationV2 {

    protected void validatePermissionsPresence(List<String> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            throw new MissingRequiredFieldException("A lista de permissões é obrigatória e não pode estar vazia.");
        }
    }

    protected void validatePermissionsFormat(List<String> permissions) {
        boolean hasInvalid = permissions.stream()
                .anyMatch(p -> !p.equals(p.trim()) || !p.equals(p.toUpperCase()));
        if (hasInvalid) {
            throw new InvalidPermissionsException("Permissões devem estar em letras maiúsculas e sem espaços.");
        }
    }

    protected void validateAtLeastOnePermissionPresent(List<String> permissions) {
        if (permissions.isEmpty()) {
            throw new MissingRequiredFieldException("É necessário informar ao menos uma permissão.");
        }
    }

    protected void validateKnownPermissions(List<String> permissions) {
        Set<String> validPermissions = PermissionRules.getAllPermissions();
        permissions.stream()
                .filter(p -> !validPermissions.contains(p))
                .findAny()
                .ifPresent(invalid -> {
                    throw new InvalidPermissionsException("Permissão inválida: " + invalid);
                });
    }
}
