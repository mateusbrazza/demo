package br.com.seuprojeto.consents.exceptions;

import br.com.seuprojeto.consents.dto.response.*;
import br.com.seuprojeto.consents.exceptions.core.ApiException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ResponseError> handleApiException(ApiException ex) {
        log.error("API Exception - {}: {}", ex.getCode(), ex.getMessage());

        ErrorDetail error = ErrorDetail.builder()
                .code(ex.getCode())
                .title(ex.getTitle())
                .detail(ex.getMessage())
                .requestDateTime(OffsetDateTime.now(ZoneOffset.UTC).toString())
                .build();

        MetaExternalResponseV2 meta = MetaExternalResponseV2.builder()
                .totalPages(1)
                .totalRecords(1)
                .build();

        ResponseError response = ResponseError.builder()
                .errors(List.of(error))
                .meta(meta)
                .build();

        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseError> handleUnexpected(Exception ex) {
        log.error("Erro inesperado não mapeado", ex);

        ErrorDetail error = ErrorDetail.builder()
                .code("UNEXPECTED_ERROR")
                .title("Erro Interno")
                .detail(ex.getMessage())
                .requestDateTime(OffsetDateTime.now(ZoneOffset.UTC).toString())
                .build();

        MetaExternalResponseV2 meta = MetaExternalResponseV2.builder()
                .totalPages(1)
                .totalRecords(1)
                .build();

        return ResponseEntity.internalServerError().body(ResponseError.builder()
                .errors(List.of(error))
                .meta(meta)
                .build());
    }
}