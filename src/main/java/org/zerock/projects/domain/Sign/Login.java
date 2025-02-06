package org.zerock.projects.domain.Sign;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Login {

    private String userId;  // 사용자 ID (문자열 형태)
    private String password;  // 사용자 비밀번호 (문자열 형태)
    private int rememberMe;  // 로그인 상태 유지 여부 (1: 유지, 0: 유지하지 않음)

}
