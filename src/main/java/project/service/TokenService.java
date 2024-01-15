package project.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import project.config.JwtProperties;
import project.domain.Token;
import project.utils.JwtUtil;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtUtil jwtUtil;

    private final StringRedisTemplate stringRedisTemplate;

    private final JwtProperties jwtProperties;

    private Long expiredMs = 30 * 60 * 1000L; // 30분

    // 액세스 토큰 생성
    public String insertAccessToken(Token token) {
        try {
            String accessToken = JwtUtil.createAccessToken(token.getEmail(), jwtProperties.getSecret(), expiredMs);

            return accessToken;
        }catch (Exception e) {
            return e.getMessage();
        }
    }

    // 리프레시 토큰 생성
    public String insertRefreshToken(Token token) {
        try {
            String refreshToken = JwtUtil.createRefreshToken(jwtProperties.getSecret());

            ValueOperations<String, String> stringValueOperations = stringRedisTemplate.opsForValue();
            stringValueOperations.set(token.getEmail(), refreshToken); // redis 저장 -> key : 사용자 이메일, value : 리프레시 토큰 값

            return refreshToken;
        }catch (Exception e) {
            return e.getMessage();
        }
    }

    public String selectUserByToken(String email) {
        ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
        String value = stringStringValueOperations.get(email);

        if(value == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        return value;
    }

    public boolean deleteToken(String email) {
        try {
            stringRedisTemplate.delete(email);

            return true;
        }catch (Exception e) {
            return false;
        }
    }


    // 1. 액세스 토큰 검증
    public boolean validAccessToken(String accessToken) {
        try {
            // 토큰을 파싱해서 서명을 확인하고, 만료 여부를 확인함
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtProperties.getSecret()) // 비밀 키
                    .parseClaimsJws(accessToken) // jwt 파싱함
                    .getBody(); // 클레임을 얻는다.

            return true; // 기존 액세스 토큰 사용
        }catch (Exception e) {
            return false; // 새로운 액세스 토큰 발급 필요
        }
    }

    // 2. 액세스 토큰 만료 시 리프레시 토큰 검증
    public String validRefreshToken(Token token) {
        if(!validAccessToken(token.getAccessToken())) { // 액세스 토큰 검증이 false면 실행

            // 레디스에서 리프레시 토큰 값 가져오기
            ValueOperations<String, String> stringValueOperations = stringRedisTemplate.opsForValue();
            String refreshTokenValue = stringValueOperations.get(token.getEmail());

            if(refreshTokenValue != null) {
                // 리프레시 토큰이 유효하다면 새로운 액세스 토큰 생성
                return callCreateAccessToken(token);
            }
        }
        return null;
    }

    // 3. 유효할 시 액세스 토큰 생성 메서드 호출
    public String callCreateAccessToken(Token token) {
            return insertAccessToken(token);
    }
}
