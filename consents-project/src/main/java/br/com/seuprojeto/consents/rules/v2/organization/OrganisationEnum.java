package br.com.seuprojeto.consents.rules.v2.organization;

import java.util.Arrays;
import java.util.Optional;

public enum OrganisationEnum {
    ITAU("9c721898-9ec0-50f1-bf85-05075557850b", "itau"),
    ITAU_CONSIGNADO("2ed4bcc0-8a11-5034-aadf-0f05878bdc07", "itauconsignado"),
    ITAUBANK("a3826c20-efe2-5634-90f7-6ef24a2d27a7", "itaubank"),
    ITAU_CVT("a7372381-89b7-5e23-b4a2-ec72735f41f2", "itaucvt"),
    INTRAQI("97a95ddb-a6ca-58ff-9551-c371ad02731a", "intraqi"),
    ITAU_DTVM("e08dc735-236a-5b6c-8004-630c242a57cf", "itaudtvm"),
    ITAU_VEST_DTVM("67f10bc2-bda1-52d8-a74c-46ad1548d076", "itauvestdtvm"),
    ITAUCARD("b6b68aba-7b89-5981-8679-28a4244d1f54", "itaucard"),
    REDECARD("c5845a2a-3dbe-540b-b0c1-a8c60bf59637", "redecared");

    private final String orgId;
    private final String orgName;

    OrganisationEnum(String orgId, String orgName) {
        this.orgId = orgId;
        this.orgName = orgName;
    }

    public String getOrgId() { return orgId; }
    public String getOrgName() { return orgName; }

    public static OrganisationEnum getOrgByOrgName(String orgName) {
        return Arrays.stream(OrganisationEnum.values())
                .filter(org -> org.getOrgName().equals(orgName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(String.format("ORG_NOT_ALLOWED_MSG: %s", orgName)));
    }
}
