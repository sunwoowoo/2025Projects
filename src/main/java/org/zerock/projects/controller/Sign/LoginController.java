package org.zerock.projects.controller.Sign;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.zerock.projects.domain.Sign.Login;
import org.zerock.projects.service.Sign.SignService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Controller
@Log4j2
@RequiredArgsConstructor
public class LoginController {

    private final SignService signService;

    // 로그인 페이지 요청
    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    // 로그인 처리 (POST 방식)
    @PostMapping("/sign/login")
    public String login(@RequestParam("userId") String userId, @RequestParam("password") String password, HttpServletResponse response, Model model) {
        Login login = signService.login(userId, password);

        if (login != null) {
            Cookie userCookie = new Cookie("userId", userId);
            userCookie.setMaxAge(60 * 60 * 24);  // 24시간 동안 쿠키 유지
            userCookie.setPath("/");  // 모든 경로에서 쿠키 사용 가능
            response.addCookie(userCookie);

            return "redirect:/list";  // 리디렉션 경로를 /list로 변경
        } else {
            model.addAttribute("error", "아이디 또는 비밀번호가 잘못되었습니다.");
            return "login";  // loginForm.html
        }
    }

}
