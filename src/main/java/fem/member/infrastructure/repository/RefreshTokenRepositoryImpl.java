package fem.member.infrastructure.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fem.member.application.port.RefreshTokenRepository;
import fem.member.domain.RefreshToken;
import fem.member.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    @Override
    public RefreshToken getByToken(String token) {
        String jsonData = redisTemplate.opsForValue().get("RefreshToken:" + token);
        if (StringUtils.hasText(jsonData)) {
            try {
                return objectMapper.readValue(jsonData, RefreshToken.class);
            } catch (JsonProcessingException e) {
                log.error("json 변환 실패 : ", e);
                throw new RuntimeException(e);
            }
        }
        throw new ResourceNotFoundException("RefreshToken", token);
    }

    @Override
    public RefreshToken save(RefreshToken token) {
        try {
            String jsonData = objectMapper.writeValueAsString(token);
            redisTemplate.opsForValue()
                    .set("RefreshToken:" + token.getToken(), jsonData, token.getExpiration(), TimeUnit.MILLISECONDS);
        } catch (JsonProcessingException e) {
            log.error("json 변환 실패 : ", e);
            throw new RuntimeException(e);
        }

        return token;
    }

    @Override
    public void deleteByToken(String refreshToken) {
        redisTemplate.delete("RefreshToken:" + refreshToken);
    }
}

