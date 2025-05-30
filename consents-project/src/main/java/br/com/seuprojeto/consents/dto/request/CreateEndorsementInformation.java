package br.com.seuprojeto.consents.dto.request;

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
public class CreateEndorsementInformation {

    @NotEmpty
    private String policyId;

    @NotNull
    private List<@NotEmpty String> insuredObjectId;

    private String proposalId;

    @NotEmpty
    private String endorsementType; // ALTERACAO, CANCELAMENTO, INCLUSAO, EXCLUSAO

    @NotEmpty
    private String requestDescription;
}
