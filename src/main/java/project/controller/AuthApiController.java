package project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import project.config.JwtProperties;
import project.domain.Token;
import project.domain.User;
import project.service.TokenService;
import project.service.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class AuthApiController {

    private final JwtProperties jwtProperties;

    private final UserService userService;

    private final TokenService tokenService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    // 회원 가입
    @PostMapping("/api/join")
    public ResponseEntity<Map<String, Object>> insertUser(@RequestBody User user) throws Exception {
        HashMap<String, Object> resultMap = new HashMap<>();

        Token token = new Token(user);

        int insertUser = userService.insertUser(user);

        if (insertUser == 1) {
            resultMap.put("result", "success");
            resultMap.put("message", "회원가입에 성공하였습니다.");
        } else {
            resultMap.put("result", "error");
            resultMap.put("message", "회원가입이 실패했습니다.");
        }

        return ResponseEntity.ok(resultMap);
    }

    // 로그인
    @PostMapping("/api/login")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> selectUser(@RequestBody User user) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        Token token = new Token(user); // 요청 온 user 객체 속 email를 token 객체 속 email로 매핑

        Optional<User> findById = userService.findById(user.getEmail());

        if (findById.isPresent()) { // Optional이 비어있지 않은지 확인
            User foundUser = findById.get();

            // 비밀번호 검증
            if (bCryptPasswordEncoder.matches(user.getPassword(), foundUser.getPassword())) {
                // 토큰 생성
                resultMap.put("AccessToken", tokenService.insertAccessToken(token)); // 액세스 토큰 생성
                resultMap.put("RefreshToken", tokenService.insertRefreshToken(token)); // 리프레쉬 토큰 생성

                resultMap.put("userEmail", user.getEmail());
                resultMap.put("result", "success");
                resultMap.put("message", "로그인에 성공하였습니다.");
            } else {
                throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
            }
        } else {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }

        return ResponseEntity.ok(resultMap);
    }

    // 회원 정보 수정
    @PostMapping("/api/update/Info")
    public ResponseEntity<Map<String, Object>> updateUser(@RequestBody User user, @RequestHeader(name = "Authorization") String authorization) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        Token tokenInstance = new Token(user);

        String accessToken = authorization.replace("Bearer ", ""); // 클라이언트에서 요청 헤더에 포함한 액세스 토큰을 받아옴

        if(tokenService.validAccessToken(accessToken)) {
            int updateUser = userService.updateUser(user);

            if(updateUser == 1) {
                resultMap.put("result", "success");
                resultMap.put("AccessToken", accessToken);
                resultMap.put("message", "회원 정보 수정 완료되었습니다.");
            } else {
                resultMap.put("result", "error");
                resultMap.put("message", "회원 정보 수정 실패했습니다.");
            }
        } else if(tokenService.validRefreshToken(tokenInstance) != null) {// 액세스 토큰이 유효하지 않아서 false 리턴 시 리프레시 토큰 검증
                // 리프레시 토큰이 유효한 경우, 새로운 액세스 토큰 발급
                String newAccessToken = tokenService.callCreateAccessToken(tokenInstance);

                if (newAccessToken != null) {
                    // 새로운 액세스 토큰이 발급되었으면 회원 정보 수정 진행
                    int updateUser = userService.updateUser(user);

                    if (updateUser == 1) {
                        resultMap.put("AccessToken", newAccessToken);
                        resultMap.put("result", "success");
                        resultMap.put("message", "회원 정보 수정 완료되었습니다.");
                    } else {
                        resultMap.put("result", "error");
                        resultMap.put("message", "회원 정보 수정 실패했습니다.");
                    }

                } else {
                    resultMap.put("result", "error");
                    resultMap.put("message", "새로운 액세스 토큰 발급 실패");
                }

        } else {
            resultMap.put("result", "error");
            resultMap.put("message", "액세스 토큰 및 리프레시 토큰이 모두 유효하지 않습니다.");
        }

        return ResponseEntity.ok(resultMap);
    }

    // 회원 탈퇴
    @PostMapping("/api/delete/Info")
    public ResponseEntity<Map<String, Object>> deleteUser(@RequestBody User user) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        int deleteUser = userService.deleteUser(user.getEmail());

        if(deleteUser == 1) {
            // 토큰 값 제거
            if(tokenService.deleteToken(user.getEmail())) {
                resultMap.put("result", "success!");
                resultMap.put("message", "회원 탈퇴가 완료됐습니다.");
            } else {
                resultMap.put("result", "not del token");
                resultMap.put("message", "토큰 삭제 실패했습니다.");
            }
        }else {
            resultMap.put("result", "error");
            resultMap.put("message", "회원 탈퇴가 실패했습니다.");
        }

        return ResponseEntity.ok(resultMap);
    }
}