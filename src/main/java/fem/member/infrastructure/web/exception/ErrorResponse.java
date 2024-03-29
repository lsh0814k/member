package fem.member.infrastructure.web.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.validation.FieldError;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Builder(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class ErrorResponse {
    private final int code;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<FieldErrorResponse> errors;

    public static ErrorResponse of(int code, String message, List<FieldError> errors) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .errors(errors.stream().map(FieldErrorResponse::of).toList())
                .build();
    }

    public static ErrorResponse of(int code, String message) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .build();
    }

    @Getter
    @Builder
    @AllArgsConstructor
    static class FieldErrorResponse {
        private final String field;
        private final String message;

        public static FieldErrorResponse of(FieldError fieldError) {
            return FieldErrorResponse.builder()
                    .field(fieldError.getField())
                    .message(fieldError.getDefaultMessage())
                    .build();
        }
    }

}
