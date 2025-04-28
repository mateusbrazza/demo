package br.com.seuprojeto.consents.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateWithdrawalCapitalizationInformation {

    @NotEmpty
    private String capitalizationTitleName;

    @NotEmpty
    private String planId;

    @NotEmpty
    private String titleId;

    @NotEmpty
    private String seriesId;

    @NotEmpty
    private String termEndDate; // formato yyyy-MM-dd

    @NotEmpty
    private String withdrawalReason; // Enum específico

    private String withdrawalReasonOthers;

    @NotNull
    private CreateAmountDetails withdrawalTotalAmount;
}
