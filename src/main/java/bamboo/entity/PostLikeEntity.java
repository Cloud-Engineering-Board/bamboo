package bamboo.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "post_like")
public class PostLikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_like_no")
    private Long postLikeNo;

    @Column(name = "user_no")
    private Long userNo;

    @Column(name = "post_no")
    private Long postNo;
}
