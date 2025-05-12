package br.com.seuprojeto.consents.dto.rejection;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import br.com.seuprojeto.consents.enums.EnumReasonCode;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RejectReasonInternalV2 {

    private EnumReasonCode code;
    private String additionalInformation;

    public RejectReasonInternalV2() {
    }

    public RejectReasonInternalV2(EnumReasonCode code, String additionalInformation) {
        this.code = code;
        this.additionalInformation = additionalInformation;
    }

    public EnumReasonCode getCode() {
        return this.code;
    }

    public void setCode(EnumReasonCode code) {
        this.code = code;
    }

    public String getAdditionalInformation() {
        return this.additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RejectReasonInternalV2)) return false;
        RejectReasonInternalV2 that = (RejectReasonInternalV2) o;
        return code == that.code &&
                Objects.equals(additionalInformation, that.additionalInformation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, additionalInformation);
    }

    @Override
    public String toString() {
        return "RejectReasonInternalV2{" +
                "code=" + code +
                ", additionalInformation='" + additionalInformation + '\'' +
                '}';
    }
}
