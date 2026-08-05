package ru.kushedusound.service.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String PREFIX = "blacklist:jti:";

    public void addToBlacklist(String jti, Date expiration){
        long ttlMillis = expiration.getTime() - System.currentTimeMillis();
        if (ttlMillis <= 0) return;

        redisTemplate.opsForValue().set(
                PREFIX + jti,
                "revoked",
                Duration.ofMillis(ttlMillis)
        );
    }

    public boolean isBlacklisted(String jti){
        return Boolean.TRUE.equals(redisTemplate.hasKey(PREFIX + jti));
    }
}