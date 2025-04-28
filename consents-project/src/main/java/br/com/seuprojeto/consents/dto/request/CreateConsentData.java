package br.com.seuprojeto.consents.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateConsentData {

    @NotEmpty
    private List<String> permissions;

    @Valid
    private CreateLoggedUser loggedUser;

    @Valid
    private CreateBusinessEntity businessEntity;

    @NotNull
    private String expirationDateTime;

    @Valid
    private CreateEndorsementInformation endorsementInformation;

    @Valid
    private CreateClaimNotificationInformation claimNotificationInformation;

    @Valid
    private CreateWithdrawalCapitalizationInformation withdrawalCaptalizationInformation;

    @Valid
    private CreateWithdrawalLifePensionInformation withdrawalLifePensionInformation;

    @Valid
    private CreateRaffleCapitalizationTitleInformation raffleCaptalizationTitleInformation;
}
