package br.com.seuprojeto.consents.rules.v2;

import br.com.seuprojeto.consents.dto.request.*;
import br.com.seuprojeto.consents.exceptions.auth.UnauthorizedException;
import br.com.seuprojeto.consents.exceptions.business.MixedPermissionPhasesException;
import br.com.seuprojeto.consents.exceptions.business.MultiplePhase3GroupsException;
import br.com.seuprojeto.consents.exceptions.validation.InvalidPermissionsException;
import br.com.seuprojeto.consents.exceptions.validation.MissingRequiredFieldException;
import br.com.seuprojeto.consents.exceptions.validation.MissingRequiredHeaderException;
import br.com.seuprojeto.consents.utils.ConsentUtils;

import java.util.*;
import java.util.stream.Collectors;

import static br.com.seuprojeto.consents.utils.DateUtils.validateBeforeNow;
import static br.com.seuprojeto.consents.utils.DateUtils.validateGreaterThanOneYear;
import static br.com.seuprojeto.consents.utils.RulesConstants.*;

public class RulesOpinV2 extends PermissionsValidationV2 {

    public void validateConsent(OpinExternalRequestV2 req, Map<String, String> headers) {
        RulesCommon.validateHeaderFapiInteractionId(headers);

        List<String> permissions = req.getData().getPermissions();

        validateBasicHeaders(headers);
        validateBeforeNow(req.getData().getExpirationDateTime());
        validateGreaterThanOneYear(req.getData().getExpirationDateTime());

        validatePermissionsPresence(permissions);
        validatePermissionsFormat(permissions);
        validateKnownPermissions(permissions);
        validateAtLeastOnePermissionPresent(permissions);

        validateNoMixPFandPJ(req);

        Set<String> permissionSet = new HashSet<>(permissions);

        if (PermissionRules.hasFase2AndFase3(permissionSet)) {
            throw new MixedPermissionPhasesException();
        }

        if (!PermissionRules.hasAllPermissionsForFase3Group(permissionSet)) {
            throw new MissingRequiredFieldException("Permissões obrigatórias de um agrupamento da Fase 3 ausentes.");
        }

        if (PermissionRules.isOnlyFase2(permissionSet) && PermissionRules.hasMissingFase2Obligatory(permissionSet)) {
            throw new MissingRequiredFieldException("Permissão obrigatória RESOURCES_READ ausente.");
        }

        if (PermissionRules.hasOnlyResourcesRead(permissionSet)) {
            throw new InvalidPermissionsException("RESOURCES_READ não pode ser enviada sozinha.");
        }

        validateSinglePhase3Group(permissionSet);
    }

    private void validateSinglePhase3Group(Set<String> permissions) {
        Set<String> groups = permissions.stream()
                .flatMap(p -> PermissionCatalog.FASE3_REQUIRED_BY_GROUP.entrySet().stream()
                        .filter(entry -> entry.getValue().contains(p))
                        .map(Map.Entry::getKey))
                .collect(Collectors.toSet());

        if (groups.size() > 1) {
            throw new MultiplePhase3GroupsException();
        }
    }

    private void validateNoMixPFandPJ(OpinExternalRequestV2 req) {
        CreateConsentData data = req.getData();

        String rel = extractRel(data);
        validateRelValue(rel);
        validateBusinessEntityRules(rel, data);
        validatePermissionCompatibility(rel, data.getPermissions());
    }

    private String extractRel(CreateConsentData data) {
        return Optional.ofNullable(data)
                .map(CreateConsentData::getLoggedUser)
                .map(CreateLoggedUser::getDocument)
                .map(CreateDocument::getRel)
                .orElse("");
    }

    private void validateRelValue(String rel) {
        if (!ConsentUtils.isPessoaFisica(rel) && !ConsentUtils.isPessoaJuridica(rel)) {
            throw new InvalidPermissionsException("Tipo de usuário inválido (rel deve ser CPF ou CNPJ).");
        }
    }

    private void validateBusinessEntityRules(String rel, CreateConsentData data) {
        if (ConsentUtils.isPessoaFisica(rel) && data.getBusinessEntity() != null) {
            throw new InvalidPermissionsException("Pessoa física não pode conter o campo businessEntity.");
        }
        if (ConsentUtils.isPessoaJuridica(rel) && data.getBusinessEntity() == null) {
            throw new InvalidPermissionsException("Pessoa jurídica deve conter o campo businessEntity.");
        }
    }

    private void validatePermissionCompatibility(String rel, List<String> permissions) {
        for (String permission : permissions) {
            if (ConsentUtils.isPessoaFisica(rel) && ConsentUtils.isPermissaoDePessoaJuridica(permission)) {
                throw new InvalidPermissionsException("Pessoa física não pode usar permissões de pessoa jurídica (BUSINESS).");
            }
            if (ConsentUtils.isPessoaJuridica(rel) && ConsentUtils.isPermissaoDePessoaFisica(permission)) {
                throw new InvalidPermissionsException("Pessoa jurídica não pode usar permissões de pessoa física (PERSONAL).");
            }
        }
    }

    public void validateBasicHeaders(Map<String, String> headers) {
        String authorization = headers.get(AUTHORIZATION_HEADER);
        String fapiInteractionId = headers.get(X_FAPI_INTERACTION_ID);
        String idempotencyKey = headers.get(X_IDEMPOTENCY_KEY);

        if (authorization == null || authorization.isBlank()) {
            throw new UnauthorizedException("Authorization header is missing or empty.");
        }

        if (fapiInteractionId == null || fapiInteractionId.isBlank()) {
            throw new MissingRequiredHeaderException("x-fapi-interaction-id header is missing or empty.");
        }

        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new MissingRequiredHeaderException("x-idempotency-key header is missing or empty.");
        }
    }
}
