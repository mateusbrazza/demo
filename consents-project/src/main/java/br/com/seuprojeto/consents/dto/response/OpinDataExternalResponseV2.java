package br.com.seuprojeto.consents.dto.response;


import br.com.seuprojeto.consents.dto.rejection.RejectionInternalV2;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpinDataExternalResponseV2 {
    private String consentId;
    private String creationDateTime;
    private String statusUpdateDateTime;
    private String status;
    // private CreateBusinessEntity businessEntity;
    private RejectionInternalV2 rejection;
    private List<String> permissions;
    private String expirationDateTime;
}
