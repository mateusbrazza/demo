package br.com.seuprojeto.consents.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa o documento de identificação (CPF ou CNPJ).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDocument {

    @NotEmpty
    private String identification;

    @NotEmpty
    private String rel;
}
