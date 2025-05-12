//package br.com.seuprojeto.consents.validation;
//
//import br.com.seuprojeto.consents.dto.request.OpinExternalRequestV2;
//import br.com.seuprojeto.consents.exceptions.auth.UnauthorizedException;
//import br.com.seuprojeto.consents.exceptions.validation.MissingRequiredHeaderException;
//import br.com.seuprojeto.consents.rules.v2.RulesOpinV2;
//import br.com.seuprojeto.consents.utils.DateUtils;
//import org.springframework.stereotype.Service;
//
//import java.util.Map;
//
//import static br.com.seuprojeto.consents.utils.RulesConstants.*;
//
//@Service
//public class OpinValidationService {
//
//    private final RulesOpinV2 rulesOpinV2 = new RulesOpinV2();
//
//    public void validateCreateConsentRequest(OpinExternalRequestV2 request, Map<String, String> headers) {
//        rulesOpinV2.validateConsent(request, headers);
//    }
//
//    public void validateExpirationDateTime(String expirationDateTime) {
//        DateUtils.validateBeforeNow(expirationDateTime);
//        DateUtils.validateGreaterThanOneYear(expirationDateTime);
//    }
//
//
//}
