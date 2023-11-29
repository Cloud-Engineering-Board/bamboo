package bamboo.util.mapper;

import bamboo.dto.request.RequestPostDTO;
import bamboo.dto.response.PostDTO;
import bamboo.entity.PostEntity;
import bamboo.entity.UserEntity;
import bamboo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostMapper {

    private final UserRepository userRepository;

    public PostEntity dtoToEntity(RequestPostDTO requestPostDTO){
        log.info("[postDtoToEntity] postDtoToEntity start");
        Date now = new Date();
        PostEntity postEntity = PostEntity.builder()
                .title(requestPostDTO.getTitle())
                .content(requestPostDTO.getContent())
                .category(requestPostDTO.getCategory())
                .createdAt(Long.toString(now.getTime()))
                .views(0L)
                .status(0)
                .build();
        log.info("[postDtoToEntity] postDtoToEntity done, Entity : {}", postEntity);
        return postEntity;
    }

    public PostDTO entityToDTO(PostEntity postEntity){
        log.info("[postEntityToRedis] postEntityToRedis start");
        UserEntity userEntity = userRepository.findById(postEntity.getWriterNo()).get();
        PostDTO postDTO = PostDTO.builder()
                .postNo(postEntity.getPostNo())
                .title(postEntity.getTitle())
                .writer(userEntity.getNickname())
                .content(postEntity.getContent())
                .category(postEntity.getCategory())
                .createdAt(postEntity.getCreatedAt())
                .views(postEntity.getViews())
                .status(postEntity.getStatus())
                .build();

        log.info("[postEntityToRedis] postEntityToRedis done, postRedisEntity : {}", postDTO);
        return postDTO;
    }
}
