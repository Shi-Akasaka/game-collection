package com.example.retrogame;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    private final UserRepository userRepository;

    public LoginController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> login(
            @RequestBody User loginUser,
            HttpSession session) {

        User user = userRepository.findByUsername(loginUser.getUsername())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest()
                    .body("ユーザー名またはパスワードが違います");
        }

        if (!user.getPassword().equals(loginUser.getPassword())) {
            return ResponseEntity.badRequest()
                    .body("ユーザー名またはパスワードが違います");
        }

        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());

        return ResponseEntity.ok().body("ログインしました");
    }
    
    @GetMapping("/check")
    public ResponseEntity<?> checkLogin(HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).body("未ログインです");
        }

        return ResponseEntity.ok().body("ログイン中です");
    }
}
