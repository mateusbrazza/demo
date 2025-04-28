package br.com.seuprojeto.consents.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsentResponse {
    private ConsentDataResponse data;
    private LinksResponse links;
    private MetaResponse meta;
}
