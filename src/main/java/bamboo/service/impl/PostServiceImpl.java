package bamboo.service.impl;


import bamboo.dto.request.RequestPostDTO;
import bamboo.dto.response.PostDTO;
import bamboo.dto.response.User;
import bamboo.entity.PostEntity;
import bamboo.entity.PostLikeEntity;
import bamboo.exception.CustomException;
import bamboo.repository.PostLikeRepository;
import bamboo.repository.PostRepository;
import bamboo.service.PostService;
import bamboo.util.mapper.PostMapper;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final AmazonS3 amazonS3;
    private final PostMapper postMapper;
    private final PostLikeRepository postLikeRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;


    @Override
    public String addImage(MultipartFile img) throws CustomException {
        log.info("[addImage] addImage start");
        try{
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(img.getSize());
            metadata.setContentType(img.getContentType());

            amazonS3.putObject(bucket+"/post", img.getName(), img.getInputStream(), metadata);
            log.info("[addImage] addImage done");
        }catch(IOException e){
            throw new CustomException();
        }
        return amazonS3.getUrl(bucket+"/post", img.getName()).toString();
    }


    @Override
    public Long addPost(User user, RequestPostDTO requestPostDTO) throws CustomException{
        log.info("[addPost] addPost start, post : {}", requestPostDTO);
        try {
            PostEntity postEntity = postMapper.dtoToEntity(requestPostDTO);
            postEntity.setWriterNo(user.getId());
            postEntity = postRepository.save(postEntity);
            log.info("[addPost] addPost done");
            return postEntity.getPostNo();
        }catch(Exception e){
            throw new CustomException();
        }
    }

    @Override
    public PostDTO findByPostNo(Long userId, Long postNo) throws CustomException{
        log.info("[findByPostNo] findByPostNo start");
        PostEntity postEntity = postRepository.findById(postNo).orElseThrow(CustomException::new);
        postEntity.setViews(postEntity.getViews()+1);
        postRepository.save(postEntity);
        PostDTO postDTO = postMapper.entityToDTO(postEntity);

        postDTO.setLikes(postLikeRepository.countByPostNo(postNo));
        if(postLikeRepository.findByUserNoAndPostNo(userId, postNo).isPresent()){
            postDTO.setLike(true);
        }
        if(postEntity.getWriterNo().equals(userId)){
            postDTO.setMine(true);
        }

        log.info("[findByPostNo] findByPostNo done");
        return postDTO;
    }


    @Override
    public List<PostDTO> findByCategory(int category) throws CustomException{
        log.info("[findByCategory] findByCategory start, category : {}", category);
        List<PostDTO> list = postRepository.findByCategory(category).stream()
                .map(postMapper::entityToDTO).collect(Collectors.toList());
        log.info("[findByCategory] findByCategory done, count : {}", list.size());
        return list;
    }

    @Override
    public Long putPost(RequestPostDTO requestPostDTO) throws CustomException {
        log.info("[putPost] putPost start, post : {}", requestPostDTO);
        PostEntity postEntity = postRepository.findById(requestPostDTO.getPostNo()).get();
        postEntity.setTitle(requestPostDTO.getTitle());
        postEntity.setContent(requestPostDTO.getContent());
        postRepository.save(postEntity);
        log.info("[putPost] putPost done, post : {}", requestPostDTO);
        return requestPostDTO.getPostNo();
    }

    @Override
    public void deleteByPostNo(Long postNo){
        log.info("[deleteByPostNo] deleteByPostNo start");
        postRepository.deleteById(postNo);
        log.info("[deleteByPostNo] deleteByPostNo done");
    }

    @Override
    public boolean like(Long userId, Long postNo) {
        log.info("[like] like start");
        Optional<PostLikeEntity> optional = postLikeRepository.findByUserNoAndPostNo(userId, postNo);
        if(optional.isPresent()){
            PostLikeEntity postLikeEntity = optional.get();
            postLikeRepository.deleteById(postLikeEntity.getPostLikeNo());
            log.info("[like] don't like");
            return false;
        }
        postLikeRepository.save(PostLikeEntity
                .builder()
                .postNo(postNo)
                .userNo(userId)
                .build());
        log.info("[like] like");
        return true;
    }

    @Override
    public List<PostDTO> findByWriterNo(Long writerNo) {
        return postRepository.findByWriterNo(writerNo).stream().map(postMapper::entityToDTO).collect(Collectors.toList());
    }
}
