package bamboo.exception;

import lombok.Getter;

import javax.servlet.ServletException;

@Getter
public class TokenException extends ServletException {

    private ErrorCode errorCode;
    public TokenException(String msg) {
        super(msg);
    }

    public TokenException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
