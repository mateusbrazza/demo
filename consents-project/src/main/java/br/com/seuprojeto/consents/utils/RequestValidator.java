package br.com.seuprojeto.consents.utils;

import br.com.seuprojeto.consents.exceptions.core.ApiException;
import br.com.seuprojeto.consents.exceptions.validation.RequestSchemaValidationException;
import br.com.seuprojeto.consents.exceptions.validation.InvalidJsonConversionException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.*;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public final class RequestValidator {

    private final ObjectMapper mapper;

    public RequestValidator(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public void validatePostConsentRequestBody(Object req, String schemaPath) {
        try {
            JsonNode jsonNode = convertRequestToJsonNode(req);
            Set<ValidationMessage> errors = validateJson(jsonNode, schemaPath);
            if (!errors.isEmpty()) {
                throw new RequestSchemaValidationException(errors);
            }
        } catch (RequestSchemaValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidJsonConversionException("Erro ao converter o corpo da requisição para JSON: " + e.getMessage());
        }
    }

    private JsonNode convertRequestToJsonNode(Object req) throws com.fasterxml.jackson.core.JsonProcessingException {
        return mapper.readTree(mapper.writeValueAsString(req));
    }

    private Set<ValidationMessage> validateJson(JsonNode jsonNode, String schemaPath) {
        SchemaValidatorsConfig config = new SchemaValidatorsConfig();
        config.setFormatAssertionsEnabled(true);
        config.enableUnevaluatedProperties();

        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
        JsonSchema jsonSchema = factory.getSchema(RequestValidator.class.getResourceAsStream(schemaPath), config);

        return jsonSchema.validate(jsonNode);
    }
}
