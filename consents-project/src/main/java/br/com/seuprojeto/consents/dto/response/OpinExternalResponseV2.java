package br.com.seuprojeto.consents.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpinExternalResponseV2 implements Serializable {
    private static final long serialVersionUID = 1L;

    private OpinDataExternalResponseV2 data;
    private LinksExternalResponseV2 links;
    private MetaExternalResponseV2 meta;

    public OpinExternalResponseV2() {}

    public OpinExternalResponseV2(OpinDataExternalResponseV2 data, LinksExternalResponseV2 links, MetaExternalResponseV2 meta) {
        this.data = data;
        this.links = links;
        this.meta = meta;
    }

    public OpinDataExternalResponseV2 getData() { return this.data; }
    public void setData(OpinDataExternalResponseV2 data) { this.data = data; }

    public LinksExternalResponseV2 getLinks() { return this.links; }
    public void setLinks(LinksExternalResponseV2 links) { this.links = links; }

    public MetaExternalResponseV2 getMeta() { return this.meta; }
    public void setMeta(MetaExternalResponseV2 meta) { this.meta = meta; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OpinExternalResponseV2)) return false;
        OpinExternalResponseV2 that = (OpinExternalResponseV2) o;
        return Objects.equals(getData(), that.getData()) &&
                Objects.equals(getLinks(), that.getLinks()) &&
                Objects.equals(getMeta(), that.getMeta());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getData(), getLinks(), getMeta());
    }

    @Override
    public String toString() {
        return "OpinExternalResponseV2{" +
                "data=" + data +
                ", links=" + links +
                ", meta=" + meta +
                '}';
    }
}
