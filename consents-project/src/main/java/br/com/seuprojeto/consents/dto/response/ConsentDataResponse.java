package br.com.seuprojeto.consents.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsentDataResponse {
    private String consentId;
    private OffsetDateTime creationDateTime;
    private String status;
    private OffsetDateTime statusUpdateDateTime;
    private List<String> permissions;
    private OffsetDateTime expirationDateTime;
}
