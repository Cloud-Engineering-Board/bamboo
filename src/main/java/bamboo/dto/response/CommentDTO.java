package bamboo.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Long commentNo;
    private Long postNo;
    private String content;
    private String CreatedAt;
    private String writer;
    private String writerImg;
    private int likes;
    private boolean like;
    private boolean mine;
}
