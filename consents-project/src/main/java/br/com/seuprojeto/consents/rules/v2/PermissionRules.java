package br.com.seuprojeto.consents.rules.v2;


import br.com.seuprojeto.consents.enums.PermissionEnum;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PermissionRules {

    public static Set<String> getAllPermissions() {
        return Stream.concat(
                PermissionEnum.getFase2Permissions().stream(),
                PermissionEnum.getFase3Permissions().stream()
        ).collect(Collectors.toSet());
    }

    public static boolean hasMissingFase2Obligatory(Set<String> permissions) {
        return !permissions.containsAll(PermissionCatalog.FASE2_OBRIGATORIAS);
    }

    public static boolean hasOnlyResourcesRead(Set<String> permissions) {
        return permissions.size() == 1 && permissions.contains("RESOURCES_READ");
    }

    public static boolean isOnlyFase2(Set<String> permissions) {
        return permissions.stream().allMatch(PermissionEnum.getFase2Permissions()::contains);
    }

    public static boolean isFase3Permission(String permission) {
        return PermissionEnum.getFase3Permissions().contains(permission);
    }

    public static boolean hasFase2AndFase3(Set<String> permissions) {
        return permissions.stream().anyMatch(PermissionEnum.getFase2Permissions()::contains) &&
                permissions.stream().anyMatch(PermissionRules::isFase3Permission);
    }

    public static boolean hasAllPermissionsOfGroup(Set<String> permissions, String groupKey) {
        Set<String> required = PermissionCatalog.FASE2_REQUIRED_GROUPS.get(groupKey);
        return required == null || permissions.containsAll(required);
    }

    public static boolean hasAllPermissionsForFase3Group(Set<String> permissions) {
        return PermissionCatalog.FASE3_REQUIRED_BY_GROUP.values().stream().noneMatch(group ->
                permissions.stream().anyMatch(group::contains) &&
                        !permissions.containsAll(group)
        );
    }
}
