package org.zerock.projects.service.Sign;

import org.zerock.projects.domain.Sign.Login;
import org.springframework.stereotype.Service;

@Service
public class SignService {

    private static final String ADMIN_USER_ID = "1234";  // Changed to String literal
    private static final String ADMIN_PASSWORD = "1234";  // Changed to String literal

    // 로그인 처리 (관리자만 허용)
    public Login login(String userId, String password) {
        if (!userId.equals(ADMIN_USER_ID)) {  // Use .equals() to compare string content
            System.out.println("Login failed: Invalid userId " + userId);
            return null; // 잘못된 사용자 ID
        }
        if (password.equals(ADMIN_PASSWORD)) {  // Use .equals() to compare string content
            return new Login(userId, password, 1);  // 로그인 성공
        }
        return null;  // 로그인 실패 (비밀번호 불일치)
    }
}
