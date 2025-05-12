package br.com.seuprojeto.consents.rules.v2;

public enum ConsentStatusDirectoryEnum {
    PENDING("AWAITING_AUTHORISATION", "pending"),
    ACCEPTED("AUTHORISED", "accepted"),
    DENIED("REJECTED", "denied"),
    REVOKED("REJECTED", "revoked");

    public final String description;

    public String getInternalDescription() {
        return internalDescription;
    }

    public final String internalDescription;

    ConsentStatusDirectoryEnum(String description, String internalDescription) {
        this.description = description;
        this.internalDescription = internalDescription;
    }

    public String getDescription() {
        return description;
    }
}
