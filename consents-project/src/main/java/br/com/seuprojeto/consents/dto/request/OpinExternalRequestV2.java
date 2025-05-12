package br.com.seuprojeto.consents.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OpinExternalRequestV2 {

    @NotNull
    private CreateConsentData data;
}
