package bamboo.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDTO {
    private int status;
    private String nickname;
    private String name;
    private String profileImg;
    private String birth;
    private String email;
    private int role;

    private List<PostDTO> postList;
    private List<CommentDTO> commentList;
}
