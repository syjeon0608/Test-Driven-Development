package io.hhplus.tdd;

import io.hhplus.tdd.point.exception.InvalidChargeAmountException;
import io.hhplus.tdd.point.exception.UserNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
class ApiControllerAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return ResponseEntity.status(500).body(new ErrorResponse("500", "에러가 발생했습니다."));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException e) {
        return ResponseEntity.status(404).body(new ErrorResponse("404", e.getMessage()));
    }

    @ExceptionHandler(InvalidChargeAmountException.class)
    public ResponseEntity<ErrorResponse> handleInvalidChargeAmountException(InvalidChargeAmountException e) {
        return ResponseEntity.status(404).body(new ErrorResponse("404", e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(400).body(new ErrorResponse("400", e.getMessage()));
    }
}
