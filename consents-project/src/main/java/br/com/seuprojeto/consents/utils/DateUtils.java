package br.com.seuprojeto.consents.utils;


import br.com.seuprojeto.consents.exceptions.validation.DateTimeValidationException;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static br.com.seuprojeto.consents.utils.RulesConstants.*;


@Component
public final class DateUtils {

    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public static LocalDateTime convertToLocalDateTime(String dateToConvertString) {
        try {
            return LocalDateTime.parse(dateToConvertString, DATE_TIME_FORMAT);
        } catch (Exception e) {
            throw new DateTimeValidationException(ERROR_DATETIME_CONVERTION + dateToConvertString);
        }
    }

    public static void validateBeforeNow(String expirationDate) {
        LocalDateTime expirationDateTime = convertToLocalDateTime(expirationDate);
        if (expirationDateTime.isBefore(LocalDateTime.now())) {
            throw new DateTimeValidationException(EXPIRATION_DATE_BEFORE_TODAY);
        }
    }

    public static void validateGreaterThanOneYear(String expirationDate) {
        LocalDateTime expirationDateTime = convertToLocalDateTime(expirationDate);
        if (isExpirationDateMoreThanOneYear(expirationDateTime)) {
            throw new DateTimeValidationException(EXPIRATION_DATE_GREATER_THAN_ONE_YEAR);
        }
    }

    private static boolean isExpirationDateMoreThanOneYear(LocalDateTime expirationDateTime) {
        LocalDateTime dateTimeOneYear = LocalDateTime.now().plusYears(1);
        return expirationDateTime.isAfter(dateTimeOneYear);
    }
}
