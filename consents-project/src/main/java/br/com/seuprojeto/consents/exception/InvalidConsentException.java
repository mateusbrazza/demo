package br.com.seuprojeto.consents.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY) // 422
public class InvalidConsentException extends RuntimeException {

    private final String code;
    private final String title;
    private final String detail;

    public InvalidConsentException(String code, String title, String detail) {
        super(detail);
        this.code = code;
        this.title = title;
        this.detail = detail;
    }
}
