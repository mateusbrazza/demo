package br.com.seuprojeto.consents.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa a entidade empresarial (pessoa jurídica) do consentimento.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBusinessEntity {

    @NotNull
    @Valid
    private CreateDocument document;
}
