package fem.mock;

import fem.member.application.jwt.JwtProperties;

public class StubJwtProperties {
    public static JwtProperties create() {
        return new JwtProperties(
                "1q2w3e-qwe124-qweqwe",
                "Bearer",
                "Authorization",
                "Authorization-refresh",
                6000,
                60000
        );
    }
}
