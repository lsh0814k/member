package fem.member.infrastructure.web.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class LoginResponse {
    private boolean success;
    private int code;
    private String message;
}
