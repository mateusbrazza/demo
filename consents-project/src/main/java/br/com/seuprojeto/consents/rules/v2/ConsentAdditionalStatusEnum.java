package br.com.seuprojeto.consents.rules.v2;


public enum ConsentAdditionalStatusEnum {

    // Status regulatório
    AWAITING_AUTHORISATION("AWAITING_AUTHORISATION"),
    AUTHORISED("AUTHORISED"),
    REJECTED("REJECTED");

    private final String description;

    ConsentAdditionalStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
