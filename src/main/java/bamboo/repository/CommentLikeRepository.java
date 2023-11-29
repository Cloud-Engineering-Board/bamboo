package bamboo.repository;

import bamboo.entity.CommentLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLikeEntity,Long> {

    Optional<CommentLikeEntity> findByUserNoAndCommentNo(Long userNo, Long commentNo);

    int countByCommentNo(Long commentNo);
}
