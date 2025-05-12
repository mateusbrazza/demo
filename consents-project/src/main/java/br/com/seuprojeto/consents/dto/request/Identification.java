package br.com.seuprojeto.consents.dto.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Identification {

    @JsonProperty("brand_id")
    private String brandId;

    @JsonProperty("brand_code")
    private String brandCode;

    @JsonProperty("brand_name")
    private String brandName;

    @JsonProperty("oauth_client_id")
    private String oauthClientId;

    @JsonProperty("oauth_client_name")
    private String oauthClientName;

    @JsonProperty("org_id")
    private String orgId;

    @JsonProperty("org_name")
    private String orgName;

    @JsonProperty("subject")
    private String subject;

    @JsonProperty("software_id")
    private String softwareId;

    @JsonProperty("server_org_id")
    private String serverOrgId;
}
