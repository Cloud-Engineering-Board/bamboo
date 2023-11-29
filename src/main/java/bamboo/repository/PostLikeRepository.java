package bamboo.repository;

import bamboo.entity.PostLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLikeEntity,Long> {

    Optional<PostLikeEntity> findByUserNoAndPostNo(Long userNo, Long postNo);

    int countByPostNo(Long postNo);
}
