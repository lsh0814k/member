package fem.member.infrastructure.web.exception.handler;

import fem.member.domain.exception.CertificationCodeNotMatchedException;
import fem.member.domain.exception.ResourceExistException;
import fem.member.domain.exception.ResourceNotFoundException;
import fem.member.infrastructure.web.exception.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "Invalid Request", e.getFieldErrors());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler({ResourceExistException.class})
    public ResponseEntity<ErrorResponse> badRequestException(RuntimeException e) {
        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(CertificationCodeNotMatchedException.class)
    public ResponseEntity<ErrorResponse> forbiddenException(RuntimeException e) {
        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.FORBIDDEN.value(), e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> notFoundException(RuntimeException e) {
        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.NOT_FOUND.value(), e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}
