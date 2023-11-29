package bamboo.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RequestUserDTO {
    private String name;
    private String nickname;
    private String birth;
    private String email;
}
