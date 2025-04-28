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
public class CreateWithdrawalLifePensionInformation {

    @NotEmpty
    private String certificateId;

    @NotEmpty
    private String productName;

    @NotEmpty
    private String withdrawalType; // 1_TOTAL, 2_PARCIAL

    @NotEmpty
    private String withdrawalReason; // Enum específico

    private String withdrawalReasonOthers;

    private CreateAmountDetails desiredTotalAmount;

    @NotNull
    private CreateAmountDetails pmbacAmount;
}
