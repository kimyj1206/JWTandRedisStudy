package project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.domain.User;
import project.service.UserService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthApiController {

    private final UserService userService;


    // 회원 가입
    @PostMapping("/api/join")
    public int insertUser(@RequestBody  User user) throws Exception {
        int insertUser = userService.insertUser(user);

        return insertUser;
    }

    // 로그인
    @PostMapping("/api/login")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> selectUser(@RequestBody User user) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        int selectUser = userService.selectUser(user);

        if (selectUser == 1) {
            resultMap.put("result", "success");
            resultMap.put("message", "로그인에 성공하였습니다.");
        } else {
            resultMap.put("result", "error");
            resultMap.put("message", "아이디나 비밀번호가 틀렸습니다.");
        }

        return ResponseEntity.ok(resultMap);
    }

    // 회원 정보 수정
    @PutMapping("/api/users")
    public int updateUser(User user) throws Exception {
        int updateUser = userService.updateUser(user);

        return updateUser;
    }

    // 회원 탈퇴
    @DeleteMapping("/api//users/{email}")
    public int deleteUser(@PathVariable String email) throws Exception {
        int deleteUser = userService.deleteUser(email);

        return deleteUser;
    }


}
