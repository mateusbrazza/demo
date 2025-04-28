package br.com.seuprojeto.consents.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseError {
    private List<ErrorDetail> errors;
    private MetaResponse meta; // Mesmo MetaResponse que te mostrei
}
