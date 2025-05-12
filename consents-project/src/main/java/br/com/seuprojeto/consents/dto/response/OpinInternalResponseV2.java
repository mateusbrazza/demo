package br.com.seuprojeto.consents.dto.response;


import br.com.seuprojeto.consents.dto.request.DataInternalV2;
import br.com.seuprojeto.consents.dto.request.DefinitionInternalV2;
import br.com.seuprojeto.consents.dto.request.LinksInternalV2;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpinInternalResponseV2 implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("_links")
    private LinksInternalV2 links;
    private String id;
    private String status;
    private String subject;
    private String actor;
    private String audience;
    private DefinitionInternalV2 definition;
    private DataInternalV2 data;
    private String createdDate;
    private String updatedDate;

    public OpinInternalResponseV2() {}

    public LinksInternalV2 getLinks() { return this.links; }
    public void setLinks(LinksInternalV2 links) { this.links = links; }

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
        if (!(o instanceof OpinInternalResponseV2)) return false;
        OpinInternalResponseV2 that = (OpinInternalResponseV2) o;
        return Objects.equals(getLinks(), that.getLinks()) &&
                Objects.equals(getId(), that.getId()) &&
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
        return Objects.hash(getLinks(), getId(), getStatus(), getSubject(), getActor(), getAudience(), getDefinition(), getData(), getCreatedDate(), getUpdatedDate());
    }

    @Override
    public String toString() {
        return "OpinInternalResponseV2{" +
                "links=" + links +
                ", id='" + id + '\'' +
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
