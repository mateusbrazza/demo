package br.com.seuprojeto.consents.dto.request;


import br.com.seuprojeto.consents.dto.rejection.RejectionInternalV2;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DataInternalV2 {

    private CreateLoggedUser loggedUser;
    private CreateBusinessEntity businessEntity;
    private List<String> permissions;
    private Identification identification;
    private String expirationDateTime;

    @JsonProperty("additional_status")
    private String additionalStatus;

    private RejectionInternalV2 rejection;
    private CreateEndorsementInformation endorsementInformation;
    private CreateWithdrawalCapitalizationInformation withdrawalCapitalizationInformation;
    private CreateRaffleCapitalizationTitleInformation raffleCapitalizationTitleInformation;
    private CreateWithdrawalLifePensionInformation withdrawalLifePensionInformation;
    private boolean isPhase3;
    private String version;
}
