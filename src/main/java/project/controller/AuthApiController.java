package project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
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

        Token token = new Token(user);

        Optional<User> findById = userService.findById(user.getEmail());

        if (findById.isPresent()) { // Optional이 비어있지 않은지 확인
            User foundUser = findById.get();

            // 비밀번호 검증
            if (bCryptPasswordEncoder.matches(user.getPassword(), foundUser.getPassword())) {
                // 토큰 생성
                resultMap.put("token", tokenService.insertToken(token));
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
    @PutMapping("/api/update/Info")
    public ResponseEntity<Map<String, Object>> updateUser(User user) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        int updateUser = userService.updateUser(user);

        if(updateUser == 1) {
            resultMap.put("result", "success");
            resultMap.put("message", "회원 정보 수정 완료되었습니다.");
        }else {
            resultMap.put("result", "error");
            resultMap.put("message", "회원 정보 수정 실패했습니다.");
        }

        return ResponseEntity.ok(resultMap);
    }

    // 회원 탈퇴
    @DeleteMapping("/api//users/{email}")
    public int deleteUser(@PathVariable String email) throws Exception {
        int deleteUser = userService.deleteUser(email);

        return deleteUser;
    }


}
