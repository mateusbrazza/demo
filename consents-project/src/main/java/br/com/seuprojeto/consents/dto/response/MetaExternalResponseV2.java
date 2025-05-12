package br.com.seuprojeto.consents.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetaExternalResponseV2 {
    private Integer totalRecords;
    private Integer totalPages;
}
