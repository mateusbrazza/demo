package br.com.seuprojeto.consents.exceptions.core;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class ApiException extends RuntimeException {
    private final String code;
    private final String title;
    private final HttpStatus status;

    protected ApiException(String code, String title, HttpStatus status, String detail) {
        super(detail);
        this.code = code;
        this.title = title;
        this.status = status;
    }
}