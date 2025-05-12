package br.com.seuprojeto.consents.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpinInternalRequestV2 implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String status;
    private String subject;
    private String actor;
    private String audience;
    private DefinitionInternalV2 definition;
    private DataInternalV2 data;
    private String createdDate;
    private String updatedDate;

    public OpinInternalRequestV2() {}

    @JsonCreator
    public OpinInternalRequestV2(
            @JsonProperty("id") String id,
            @JsonProperty("status") String status,
            @JsonProperty("subject") String subject,
            @JsonProperty("actor") String actor,
            @JsonProperty("audience") String audience,
            @JsonProperty("definition") DefinitionInternalV2 definition,
            @JsonProperty("data") DataInternalV2 data,
            @JsonProperty("createdDate") String createdDate,
            @JsonProperty("updatedDate") String updatedDate
    ) {
        this.id = id;
        this.status = status;
        this.subject = subject;
        this.actor = actor;
        this.audience = audience;
        this.definition = definition;
        this.data = data;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    public String getId() { return this.id; }
    public void setId(String id) { this.id = id; }

    public String getStatus() { return this.status; }
    public void setStatus(String status) { this.status = status; }

    public String getSubject() { return this.subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getActor() { return this.actor; }
    public void setActor(String actor) { this.actor = actor; }

    public String getAudience() { return this.audience; }
    public void setAudience(String audience) { this.audience = audience; }

    public DefinitionInternalV2 getDefinition() { return this.definition; }
    public void setDefinition(DefinitionInternalV2 definition) { this.definition = definition; }

    public DataInternalV2 getData() { return this.data; }
    public void setData(DataInternalV2 data) { this.data = data; }

    public String getCreatedDate() { return this.createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }

    public String getUpdatedDate() { return this.updatedDate; }
    public void setUpdatedDate(String updatedDate) { this.updatedDate = updatedDate; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OpinInternalRequestV2)) return false;
        OpinInternalRequestV2 that = (OpinInternalRequestV2) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getStatus(), that.getStatus()) &&
                Objects.equals(getSubject(), that.getSubject()) &&
                Objects.equals(getActor(), that.getActor()) &&
                Objects.equals(getAudience(), that.getAudience()) &&
                Objects.equals(getDefinition(), that.getDefinition()) &&
                Objects.equals(getData(), that.getData()) &&
                Objects.equals(getCreatedDate(), that.getCreatedDate()) &&
                Objects.equals(getUpdatedDate(), that.getUpdatedDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, subject, actor, audience, definition, data, createdDate, updatedDate);
    }

    @Override
    public String toString() {
        return "OpinInternalRequestV2{" +
                "id='" + id + '\'' +
                ", status='" + status + '\'' +
                ", subject='" + subject + '\'' +
                ", actor='" + actor + '\'' +
                ", audience='" + audience + '\'' +
                ", definition=" + definition +
                ", data=" + data +
                ", createdDate='" + createdDate + '\'' +
                ", updatedDate='" + updatedDate + '\'' +
                '}';
    }
}
