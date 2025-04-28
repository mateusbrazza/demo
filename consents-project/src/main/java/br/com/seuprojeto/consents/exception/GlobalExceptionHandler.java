package br.com.seuprojeto.consents.exception;

import br.com.seuprojeto.consents.dto.response.ErrorDetail;
import br.com.seuprojeto.consents.dto.response.MetaResponse;
import br.com.seuprojeto.consents.dto.response.ResponseError;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ResponseError> handleBadRequest(BadRequestException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), "BAD_REQUEST");
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ResponseError> handleUnauthorized(UnauthorizedException ex) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), "UNAUTHORIZED");
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ResponseError> handleForbidden(ForbiddenException ex) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage(), "FORBIDDEN");
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ResponseError> handleNotFound(NotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), "NOT_FOUND");
    }

    @ExceptionHandler(InvalidConsentException.class)
    public ResponseEntity<ResponseError> handleInvalidConsentException(InvalidConsentException ex) {
        return buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), "UNPROCESSABLE_ENTITY");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseError> handleValidationException(ConstraintViolationException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), "VALIDATION_ERROR");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseError> handleGenericException(Exception ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), "INTERNAL_SERVER_ERROR");
    }

    private ResponseEntity<ResponseError> buildErrorResponse(HttpStatus status, String detail, String code) {
        ErrorDetail error = ErrorDetail.builder()
                .code(code)
                .title(status.getReasonPhrase())
                .detail(detail)
                .requestDateTime(OffsetDateTime.now(ZoneOffset.UTC).toString())
                .build();

        return ResponseEntity.status(status)
                .body(ResponseError.builder()
                        .errors(List.of(error))
                        .meta(MetaResponse.builder()
                                .totalRecords(1)
                                .totalPages(1)
                                .build())
                        .build());
    }
}
