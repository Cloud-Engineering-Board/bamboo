package bamboo.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
public class TokenDTO {
    private String accessToken;
    private String refreshToken;
}
