package bamboo.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
public class RequestPostDTO {
    private Long postNo;
    private String title;
    private String content;
    private int category;
}
