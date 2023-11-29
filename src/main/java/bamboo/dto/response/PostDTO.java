package bamboo.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
public class PostDTO {
    private Long postNo;
    private String writer;
    private String title;
    private String content;
    private String createdAt;

    private int category;
    private long views;
    private int status;
    private int likes;

    private boolean isLike;
    private boolean mine;

}
