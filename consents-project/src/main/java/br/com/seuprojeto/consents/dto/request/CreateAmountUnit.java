package br.com.seuprojeto.consents.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAmountUnit {

    @NotEmpty
    private String code; // Ex: "BRL"

    @NotEmpty
    private String description; // Ex: "Real Brasileiro"
}
