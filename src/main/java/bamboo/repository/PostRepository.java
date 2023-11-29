package bamboo.repository;

import bamboo.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<PostEntity,Long> {

    List<PostEntity> findByCategory(int category);

    List<PostEntity> findByWriterNo(Long writerNo);
}
