package bamboo.repository;

import bamboo.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity,Long> {

    List<CommentEntity> findByPostNo(Long postNo);

    List<CommentEntity> findByWriterNo(Long writerNo);
}
