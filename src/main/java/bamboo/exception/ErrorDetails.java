package bamboo.exception;

import bamboo.dto.response.UserCheckDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ErrorDetails {
    private int code;
    private String message;
    private UserCheckDTO userCheckDTO;
}
