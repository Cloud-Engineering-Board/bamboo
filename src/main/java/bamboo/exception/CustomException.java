package bamboo.exception;

import bamboo.dto.response.UserCheckDTO;
import lombok.Getter;

@Getter
public class CustomException extends Exception{

    private ErrorCode errorCode;
    private UserCheckDTO userCheckDTO;

    public CustomException(){}
    public CustomException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    public CustomException(ErrorCode errorCode, UserCheckDTO userCheckDTO){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.userCheckDTO = userCheckDTO;
    }
}
