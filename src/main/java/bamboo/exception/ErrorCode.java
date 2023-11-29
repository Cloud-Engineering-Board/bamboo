package bamboo.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    //NOT FOUND
    NOT_FOUND_PEOPLE(404, "FISA Cloud Engineering 교육생이 아닙니다."),
    NOT_FOUND_MEMBER(401, "아직 가입하지 않은 교육생입니다. 회원가입을 진행하겠습니다."),
    NOT_MATCHED_EMAIL(400, "이미 가입한 이메일과 다릅니다. 다른 이메일로 로그인 해주세요"),

    //Authentication
    NOT_FOUND_TOKEN(401, "토큰이 없어용"),
    TOKEN_NOT_MATCHED(401, "우리가 갖고있는 토큰이랑 달라욥"),
    ACCESS_TOKEN_EXPIRED(401, "토큰 만료되었습니다. 재발급 받으세요");

    private final int code;
    private final String message;
}
