package br.com.seuprojeto.consents.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response422ErrorCreateConsents {
    private Error422Detail errors;
}
