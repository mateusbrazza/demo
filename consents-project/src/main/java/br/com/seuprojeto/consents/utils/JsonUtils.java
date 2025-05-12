package br.com.seuprojeto.consents.utils;


import br.com.seuprojeto.consents.exceptions.integration.TokenClaimExtractionException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Base64;

import static br.com.seuprojeto.consents.constants.ErrorMessages.JWT_CLAIM_EXTRACTION_ERROR;
import static br.com.seuprojeto.consents.constants.ErrorMessages.JWT_PAYLOAD_EXTRACTION_ERROR;

public class JsonUtils {

    private JsonUtils() {}

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String JWT_SECTIONS_DELIMITER_REGEX = "\\.";

    public static String decodeJwtPayload(String jwt) {
        try {
            String[] jwtSections = jwt.split(JWT_SECTIONS_DELIMITER_REGEX);
            Base64.Decoder decoder = Base64.getUrlDecoder();
            return new String(decoder.decode(jwtSections[1]));
        } catch (RuntimeException e) {
            throw new TokenClaimExtractionException(JWT_PAYLOAD_EXTRACTION_ERROR);
        }
    }

    public static String extractClaimFromPayload(String payload, String claim) {
        try {
            JsonNode decodedPayload = mapper.readTree(decodeJwtPayload(payload));
            return decodedPayload.hasNonNull(claim) ? decodedPayload.get(claim).asText() : "";
        } catch (Exception e) {
            throw new TokenClaimExtractionException(JWT_CLAIM_EXTRACTION_ERROR);
        }
    }
}
