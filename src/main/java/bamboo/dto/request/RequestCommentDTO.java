package bamboo.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RequestCommentDTO {
    private Long commentNo;
    private Long postNo;
    private String content;
}
