package br.com.seuprojeto.consents.dto.rejection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import br.com.seuprojeto.consents.enums.EnumRejected;

import java.io.Serializable;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RejectionInternalV2 implements Serializable {

    private static final long serialVersionUID = 1L;

    private EnumRejected rejectedBy;
    private RejectReasonInternalV2 reason;

    public RejectionInternalV2() {
    }

    public RejectionInternalV2(EnumRejected rejectedBy, RejectReasonInternalV2 reason) {
        this.rejectedBy = rejectedBy;
        this.reason = reason;
    }

    public EnumRejected getRejectedBy() {
        return this.rejectedBy;
    }

    public void setRejectedBy(EnumRejected rejectedBy) {
        this.rejectedBy = rejectedBy;
    }

    public RejectReasonInternalV2 getReason() {
        return this.reason;
    }

    public void setReason(RejectReasonInternalV2 reason) {
        this.reason = reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RejectionInternalV2)) return false;
        RejectionInternalV2 that = (RejectionInternalV2) o;
        return rejectedBy == that.rejectedBy &&
                Objects.equals(reason, that.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rejectedBy, reason);
    }

    @Override
    public String toString() {
        return "RejectionInternalV2{" +
                "rejectedBy=" + rejectedBy +
                ", reason=" + reason +
                '}';
    }
}
