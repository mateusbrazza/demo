package br.com.seuprojeto.consents.dto.request;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DefinitionInternalV2 implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String version;
    private String currentVersion;
    private String locale;

    public DefinitionInternalV2() {}

    public DefinitionInternalV2(String id, String version, String currentVersion, String locale) {
        this.id = id;
        this.version = version;
        this.currentVersion = currentVersion;
        this.locale = locale;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCurrentVersion() {
        return this.currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public String getLocale() {
        return this.locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DefinitionInternalV2)) return false;
        DefinitionInternalV2 that = (DefinitionInternalV2) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(version, that.version) &&
                Objects.equals(currentVersion, that.currentVersion) &&
                Objects.equals(locale, that.locale);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, version, currentVersion, locale);
    }

    @Override
    public String toString() {
        return "DefinitionInternalV2{" +
                "id='" + id + '\'' +
                ", version='" + version + '\'' +
                ", currentVersion='" + currentVersion + '\'' +
                ", locale='" + locale + '\'' +
                '}';
    }
}
