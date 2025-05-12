package br.com.seuprojeto.consents.rules.v2.brand;


import java.util.Arrays;
import java.util.Optional;

public enum BrandEnum {
    CARTAO_LUIZA("2863356f-3efe-58b2-8a6b-5bc04ceb7a2c", "Cartao Luiza", "cartaoluiza", true, true),
    CREDICARD("b7f09147-31e1-577d-9570-1612069bd418", "Credicard", "credicard", true, true),
    HIPERCARD("adbc8ea4-eb96-51e4-85ce-3e9011b4a742", "Hipercard", "hipercard", true, true),
    ION("bb826647-9461-51d5-9681-ad3f061f8877", "Ion", "ion", true, true),
    ITAU_UNIBANCO("6141bb75-59e5-6b90-86712c35fbf0", "Itaú Unibanco", "itau", true, true),
    ITAU_BBA("b0461676-8524-5395-897c-5ac9d38e2f12", "Itaú BBA", "itaubba", true, true),
    ITAUCARD("78982146-3c40-5987-8d49-832c546f1cfc", "Itaucard", "itaucard", true, false),
    ITAU_EMPS("6067a90e-072d-5956-a8d6-005e076efb6e", "Itaú Emps", "itauemps", true, true),
    ITI("70b5929d-48a5-5cb5-9390-a387832b06dc", "Iti", "iti", true, true),
    PLAYERS_BANK("fb1435fe-4ddb-5272-a4c2-86c3ad8aa461", "Players Bank", "playersbank", true, true),
    POP("92262997-128d-5c67-aace-76ab6050f16e", "Rede pop", "pop", true, true),
    REDE("99cbd244-40ce-5595-8823-4d6f236ed4af", "Rede", "rede", true, true);

    private final String itauBrandId;
    private final String itauBrandName;
    private final String itauBrandCode;
    private final boolean pfActive;
    private final boolean pjActive;

    BrandEnum(String itauBrandId, String itauBrandName, String itauBrandCode, boolean pfActive, boolean pjActive) {
        this.itauBrandId = itauBrandId;
        this.itauBrandName = itauBrandName;
        this.itauBrandCode = itauBrandCode;
        this.pfActive = pfActive;
        this.pjActive = pjActive;
    }

    public String getItauBrandId() { return itauBrandId; }
    public String getItauBrandName() { return itauBrandName; }
    public String getItauBrandCode() { return itauBrandCode; }
    public boolean isPfActive() { return pfActive; }
    public boolean isPjActive() { return pjActive; }

    public static BrandEnum getByBrandCode(String brandCode) {
        return Arrays.stream(BrandEnum.values())
                .filter(brand -> brand.getItauBrandCode().equals(brandCode))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("BRAND_NOT_ALLOWED_MSG"));
    }
}
