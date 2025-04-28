package br.com.seuprojeto.consents.validation;

import br.com.seuprojeto.consents.dto.request.CreateConsentRequest;
import br.com.seuprojeto.consents.dto.request.CreateLoggedUser;
import br.com.seuprojeto.consents.enums.PermissionEnum;
import br.com.seuprojeto.consents.exception.BadRequestException;
import br.com.seuprojeto.consents.exception.InvalidConsentException;
import br.com.seuprojeto.consents.exception.UnauthorizedException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ConsentValidationService {

    public void validateCreateConsentRequest(CreateConsentRequest request) {
        validatePermissions(request.getData().getPermissions());
        validateExpirationDateTime(request.getData().getExpirationDateTime());
        validateLoggedUser(request.getData().getLoggedUser());
    }

    private void validatePermissions(List<String> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            throw new InvalidConsentException("Permissões obrigatórias não informadas.", "INVALID_PERMISSIONS", "A lista de permissions não pode ser vazia.");
        }

        Set<String> validPermissions = PermissionEnum.getAllPermissions();
        List<String> invalidPermissions = permissions.stream()
                .filter(p -> !validPermissions.contains(p))
                .collect(Collectors.toList());

        if (!invalidPermissions.isEmpty()) {
            throw new InvalidConsentException("Permissões inválidas fornecidas.", "INVALID_PERMISSIONS", "Permissões inválidas: " + invalidPermissions);
        }

        Set<String> fase2Permissions = PermissionEnum.getFase2Permissions();
        Set<String> fase3Permissions = PermissionEnum.getFase3Permissions();

        boolean hasFase2 = permissions.stream().anyMatch(fase2Permissions::contains);
        boolean hasFase3 = permissions.stream().anyMatch(fase3Permissions::contains);

        if (hasFase2 && hasFase3) {
            throw new InvalidConsentException("Não é permitido misturar permissões de Fase 2 e Fase 3 no mesmo consentimento.", "INVALID_PHASE_COMBINATION", "Não pode misturar Fase 2 e Fase 3.");
        }

        if (!hasFase2 && !hasFase3) {
            throw new InvalidConsentException("Permissões inválidas: deve conter permissões da Fase 2 ou da Fase 3.", "INVALID_PERMISSIONS", "Nenhuma permissão válida foi fornecida.");
        }

        if (hasFase2) {
            validateFase2(permissions);
        }

        if (hasFase3) {
            validateFase3(permissions);
        }
    }

    private void validateFase2(List<String> permissions) {
        // Se mandar só RESOURCES_READ sozinho, erro
        if (permissions.size() == 1 && permissions.contains("RESOURCES_READ")) {
            throw new InvalidConsentException(
                    "Não é permitido enviar apenas RESOURCES_READ sem permissões específicas da Fase 2.",
                    "RESOURCES_READ_ONLY",
                    "É obrigatório enviar também permissões específicas, além do RESOURCES_READ."
            );
        }

        // Se mandou permissões da Fase 2 mas não incluiu RESOURCES_READ, erro
        if (!permissions.contains("RESOURCES_READ")) {
            throw new InvalidConsentException(
                    "Para permissões da Fase 2, é obrigatório enviar também a permissão RESOURCES_READ.",
                    "MISSING_RESOURCES_READ",
                    "O consentimento de Fase 2 deve incluir RESOURCES_READ."
            );
        }
    }

    private void validateFase3(List<String> permissions) {
        Map<String, Set<String>> agrupamentosFase3 = getFase3Agrupamentos();
        Set<String> gruposEncontrados = new HashSet<>();

        for (String permission : permissions) {
            agrupamentosFase3.forEach((grupo, permissoesGrupo) -> {
                if (permissoesGrupo.contains(permission)) {
                    gruposEncontrados.add(grupo);
                }
            });
        }

        if (gruposEncontrados.size() > 1) {
            throw new InvalidConsentException(
                    "Não é permitido misturar permissões de diferentes agrupamentos de Fase 3.",
                    "INVALID_GROUPS_PHASE3",
                    "Todas as permissões da Fase 3 devem pertencer ao mesmo agrupamento."
            );
        }

        for (String grupo : gruposEncontrados) {
            Set<String> obrigatorias = agrupamentosFase3.getOrDefault(grupo, Collections.emptySet());
            boolean todasObrigatoriasPresentes = obrigatorias.stream().allMatch(permissions::contains);

            if (!todasObrigatoriasPresentes) {
                throw new InvalidConsentException(
                        "Permissões obrigatórias do agrupamento não foram informadas.",
                        "MISSING_REQUIRED_PERMISSIONS",
                        "Fase 3 exige todas as permissões obrigatórias do grupo " + grupo + "."
                );
            }
        }
    }

    private void validateExpirationDateTime(String expirationDateTime) {
        OffsetDateTime expiration;
        try {
            expiration = OffsetDateTime.parse(expirationDateTime);
        } catch (Exception e) {
            throw new InvalidConsentException("Formato inválido de data de expiração.", "INVALID_DATE_FORMAT", "O expirationDateTime deve estar em formato RFC3339.");
        }

        if (expiration.isBefore(OffsetDateTime.now())) {
            throw new InvalidConsentException("A data de expiração deve ser futura.", "EXPIRATION_IN_PAST", "O expirationDateTime deve ser maior que o momento atual.");
        }
    }

    private void validateLoggedUser(CreateLoggedUser loggedUser) {
        if (loggedUser == null || loggedUser.getDocument() == null) {
            throw new InvalidConsentException("Dados do usuário obrigatórios.", "INVALID_LOGGED_USER", "O loggedUser e document são obrigatórios.");
        }

        String rel = loggedUser.getDocument().getRel();
        String identification = loggedUser.getDocument().getIdentification();

        if (!"CPF".equalsIgnoreCase(rel) && !"CNPJ".equalsIgnoreCase(rel)) {
            throw new InvalidConsentException("Tipo de documento inválido.", "INVALID_REL", "O rel deve ser CPF ou CNPJ.");
        }

        if ("CPF".equalsIgnoreCase(rel) && (identification == null || !identification.matches("\\d{11}"))) {
            throw new InvalidConsentException("CPF inválido.", "INVALID_CPF", "O identification para CPF deve ter 11 dígitos.");
        }

        if ("CNPJ".equalsIgnoreCase(rel) && (identification == null || !identification.matches("\\d{14}"))) {
            throw new InvalidConsentException("CNPJ inválido.", "INVALID_CNPJ", "O identification para CNPJ deve ter 14 dígitos.");
        }
    }

    private Map<String, Set<String>> getFase3Agrupamentos() {
        Map<String, Set<String>> agrupamentos = new HashMap<>();

        agrupamentos.put("QUOTE_AUTO", Set.of("QUOTE_AUTO_LEAD_CREATE", "QUOTE_AUTO_LEAD_UPDATE"));
        agrupamentos.put("QUOTE_PATRIMONIAL", Set.of("QUOTE_PATRIMONIAL_LEAD_CREATE", "QUOTE_PATRIMONIAL_LEAD_UPDATE"));
        agrupamentos.put("QUOTE_HOUSING", Set.of("QUOTE_HOUSING_LEAD_CREATE", "QUOTE_HOUSING_LEAD_UPDATE"));
        agrupamentos.put("CONTRACT_PENSION", Set.of("CONTRACT_PENSION_PLAN_LEAD_CREATE", "CONTRACT_PENSION_PLAN_LEAD_UPDATE"));
        agrupamentos.put("CONTRACT_LIFE_PENSION", Set.of("CONTRACT_LIFE_PENSION_LEAD_CREATE", "CONTRACT_LIFE_PENSION_LEAD_UPDATE"));
        agrupamentos.put("CLAIM_NOTIFICATION_DAMAGE", Set.of("CLAIM_NOTIFICATION_REQUEST_DAMAGE_CREATE"));
        agrupamentos.put("CLAIM_NOTIFICATION_PERSON", Set.of("CLAIM_NOTIFICATION_REQUEST_PERSON_CREATE"));
        agrupamentos.put("ENDORSEMENT_REQUEST", Set.of("ENDORSEMENT_REQUEST_CREATE"));
        agrupamentos.put("PENSION_WITHDRAWAL", Set.of("PENSION_WITHDRAWAL_CREATE", "PENSION_WITHDRAWAL_LEAD_CREATE"));
        agrupamentos.put("CAPITALIZATION_TITLE_WITHDRAWAL", Set.of("CAPITALIZATION_TITLE_WITHDRAWAL_CREATE"));
        agrupamentos.put("PERSON_WITHDRAWAL", Set.of("PERSON_WITHDRAWAL_CREATE"));

        return agrupamentos;
    }

    // ========== MÉTODOS PRIVADOS DE VALIDAÇÃO DOS HEADERS ==========

    public void validateBasicHeaders(String authorization, String fapiInteractionId) {
        if (authorization == null || authorization.isBlank()) {
            throw new UnauthorizedException("Authorization header is missing or empty.");
        }
        if (fapiInteractionId == null || fapiInteractionId.isBlank()) {
            throw new BadRequestException("x-fapi-interaction-id header is missing or empty.");
        }
    }

    public void validateCreateHeaders(String authorization, String idempotencyKey, String fapiInteractionId) {
        validateBasicHeaders(authorization, fapiInteractionId);
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new BadRequestException("x-idempotency-key header is missing or empty.");
        }
    }

}
