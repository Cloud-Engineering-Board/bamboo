package bamboo.dto.response;

import lombok.*;

import javax.servlet.http.Cookie;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseUserDTO {
    private UserDTO user;
    private Cookie cookie;
}
