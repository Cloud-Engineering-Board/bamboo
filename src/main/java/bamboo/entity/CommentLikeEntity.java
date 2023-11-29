package bamboo.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comment_like")
public class CommentLikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_like_no")
    private Long commentLikeNo;

    @Column(name = "user_no")
    private Long userNo;

    @Column(name = "comment_no")
    private Long commentNo;
}
