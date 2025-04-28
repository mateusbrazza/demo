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
public class CreateAmountDetails {

    @NotEmpty
    private String amount; // formato regex: '^-?\\d{1,15}\\.\\d{2}$'

    @NotNull
    private CreateAmountUnit unit;
}
