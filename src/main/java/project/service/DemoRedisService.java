package project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class DemoRedisService {

    private final int LIMIT_TIME = 3 * 60; // 3분 제한 시간

    private final StringRedisTemplate stringRedisTemplate;

    // Create
    public void setRedisValue(String key, String value) {
        ValueOperations<String, String> stringValueOperations = stringRedisTemplate.opsForValue(); // ValueOperations<K, V> : redis의 문자열 자료 구조를 사용할 수 있는 기능
        stringValueOperations.set(key, value); // redis의 set과 동일
        //stringValueOperations.set(key, value, LIMIT_TIME); // 만료 시간 추가
    }

    // read
    public String getRedisValue(String key) {
        ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
        String value = stringStringValueOperations.get(key);

        if(value == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return value;
    }

    // update
    public void updateRedisValue(String key, String value) {
        ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
        stringStringValueOperations.getAndSet(key, value);
    }

    // delete
    public void deleteRedisValue(String key) {
        stringRedisTemplate.delete(key);
    }
}
