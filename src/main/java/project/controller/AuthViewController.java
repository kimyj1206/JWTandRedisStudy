package project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import project.domain.User;
import project.service.UserService;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class AuthViewController {

    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    // 전체 회원 조회
    @GetMapping("/users")
    public String findAll(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);

        return "users";
    }

    // 내 정보
    @GetMapping("/userInfo/{email}")
    public String userInfo(@PathVariable String email, Model model) {

        Optional<User> findUser = userService.findById(email);

        findUser.ifPresent(user -> model.addAttribute("users", user));

        return "userInfo";
    }
}
