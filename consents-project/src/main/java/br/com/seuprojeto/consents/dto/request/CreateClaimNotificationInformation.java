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
public class CreateClaimNotificationInformation {

    @NotEmpty
    private String documentType; // APOLICE_INDIVIDUAL, BILHETE, etc.

    @NotEmpty
    private String policyId;

    private String groupCertificateId;

    @NotNull
    private List<@NotEmpty String> insuredObjectId;

    private String proposalId;

    @NotEmpty
    private String occurrenceDate; // formato yyyy-MM-dd

    private String occurrenceTime; // formato HH:mm:ss

    @NotEmpty
    private String occurrenceDescription;
}
