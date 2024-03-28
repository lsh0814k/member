package fem.mock;

import fem.member.application.port.UuidHolder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FakeUuidHolder implements UuidHolder {
    private final String uuid;

    @Override
    public String random() {
        return uuid;
    }
}
