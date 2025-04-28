package br.com.seuprojeto.consents.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa o usuário logado.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateLoggedUser {

    @NotNull
    @Valid
    private CreateDocument document;
}
