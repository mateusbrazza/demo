package br.com.seuprojeto.consents.rules;

import br.com.seuprojeto.consents.dto.request.CreateConsentRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PermissionsValidationV2 {

protected void validateEmptyPermissions(CreateConsentRequest req) {
        if (req.getData().getPermissions() == null || req.getData().getPermissions().isEmpty()) {
        throw new PermissionsValidationException("A lista de permissões está vazia.");
        }
        }

protected void validatePermissionFormat(List<String> permissions) {
        boolean hasInvalid = permissions.stream()
        .anyMatch(p -> !p.equals(p.trim()) || !p.equals(p.toUpperCase()));
        if (hasInvalid) {
        throw new PermissionsValidationException("Permissões devem estar em letras maiúsculas e sem espaços.");
        }
        }

protected void validateMinimumPermissionCount(List<String> permissions) {
        if (permissions.size() < 1) {
        throw new PermissionsValidationException("Deve haver pelo menos uma permissão.");
        }
        }

protected void validateOnlyValidPermissions(List<String> permissions) {
        Set<String> validPermissions = new HashSet<>();
        validPermissions.addAll(PermissionCatalog.DATA_SHARING_PERMISSIONS);
        validPermissions.addAll(PermissionCatalog.SERVICE_INITIATION_PERMISSIONS);

        permissions.stream()
        .filter(p -> !validPermissions.contains(p))
        .findAny()
        .ifPresent(invalid -> {
        throw new PermissionsValidationException("Permissão inválida: " + invalid);
        });
        }

protected void validateHasRequiredGroupPermissions(List<String> permissions, List<String> requiredGroup) {
        if (!permissions.containsAll(requiredGroup)) {
        throw new PermissionsValidationException("Está faltando permissão obrigatória do grupo: " + requiredGroup);
        }
        }

protected void validateGroupBaseRule(List<String> permissions, List<String> base, List<String> dependents) {
        boolean hasDependent = permissions.stream().anyMatch(dependents::contains);
        boolean hasBase = permissions.stream().anyMatch(base::contains);

        if (hasDependent && !hasBase) {
        throw new PermissionsValidationException("Permissão base ausente para permissões dependentes.");
        }
        }

protected void validateNoMixPFandPJ(List<String> permissions) {
        boolean hasPF = permissions.stream().anyMatch(PermissionCatalog.PF::contains);
        boolean hasPJ = permissions.stream().anyMatch(PermissionCatalog.PJ::contains);
        if (hasPF && hasPJ) {
        throw new PermissionsValidationException("Não é permitido misturar permissões de PF e PJ.");
        }
        }
        }
