package br.com.seuprojeto.consents.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRaffleCapitalizationTitleInformation {

    @NotEmpty
    private String contactType; // EMAIL ou TELEFONE

    @Email
    private String email;

    private String phone;
}
