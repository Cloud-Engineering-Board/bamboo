package bamboo.service.impl;

import bamboo.dto.request.RequestCommentDTO;
import bamboo.dto.response.CommentDTO;
import bamboo.dto.response.User;
import bamboo.entity.CommentEntity;
import bamboo.entity.CommentLikeEntity;
import bamboo.entity.UserEntity;
import bamboo.repository.CommentLikeRepository;
import bamboo.repository.CommentRepository;
import bamboo.repository.UserRepository;
import bamboo.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final CommentLikeRepository commentLikeRepository;

    @Override
    public Long addComment(User user, RequestCommentDTO requestCommentDTO){
        log.info("[addComment] addComment start, commentDTO : {}", requestCommentDTO);
        Date now = new Date();

        CommentEntity commentEntity = CommentEntity.builder().writerNo(user.getId())
                .content(requestCommentDTO.getContent())
                .postNo(requestCommentDTO.getPostNo())
                .createdAt(String.valueOf(now.getTime()))
                .build();

        commentRepository.save(commentEntity);

        log.info("[addComment] addComment done");
        return requestCommentDTO.getPostNo();
    }

    @Override
    public List<CommentDTO> getComment(User user, Long postNo) {
        log.info("[getComment] getComment start");
        List<CommentDTO> list = commentRepository.findByPostNo(postNo).stream().map(e -> entityToDto(user,e)).collect(Collectors.toList());
        log.info("[getComment] getComment done, count : {}", list.size());
        return list;
    }

    public CommentDTO entityToDto(User user, CommentEntity commentEntity){
        log.info("[entityToDto] entityToDto start");
        UserEntity userEntity = userRepository.findById(commentEntity.getWriterNo()).get();
        CommentDTO commentDTO = CommentDTO.builder()
                .postNo(commentEntity.getPostNo())
                .commentNo(commentEntity.getCommentNo())
                .content(commentEntity.getContent())
                .writer(userEntity.getNickname())
                .writerImg(userEntity.getProfileImg())
                .CreatedAt(commentEntity.getCreatedAt())
                .likes(commentLikeRepository.countByCommentNo(commentEntity.getCommentNo()))
                .build();
        if(user.getId() == commentEntity.getWriterNo()){
            commentDTO.setMine(true);
        }
        if(commentLikeRepository.findByUserNoAndCommentNo(user.getId(),commentEntity.getCommentNo()).isPresent()){
            commentDTO.setLike(true);
        }
        log.info("[entityToDto] entityToDto done");
        return commentDTO;
    }

    @Override
    public Long putComment(RequestCommentDTO requestCommentDTO) {
        log.info("[putComment] putComment start");
        Date now = new Date();
        CommentEntity commentEntity = commentRepository.findById(requestCommentDTO.getCommentNo()).get();
        commentEntity.setContent(requestCommentDTO.getContent());
        commentEntity.setCreatedAt(String.valueOf(now.getTime()));
        commentRepository.save(commentEntity);
        log.info("[putComment] putComment done");
        return requestCommentDTO.getPostNo();
    }

    @Override
    public boolean deleteComment(Long commentNo) {
        log.info("[deleteComment] deleteComment start");
        commentRepository.deleteById(commentNo);
        log.info("[deleteComment] deleteComment done");
        return true;
    }

    @Override
    public boolean like(Long userId, Long commentNo) {
        log.info("[like] like start");
        Optional<CommentLikeEntity> optional = commentLikeRepository.findByUserNoAndCommentNo(userId, commentNo);
        if(optional.isPresent()){
            CommentLikeEntity commentLikeEntity = optional.get();
            commentLikeRepository.deleteById(commentLikeEntity.getCommentLikeNo());
            log.info("[like] don't like");
            return false;
        }
        commentLikeRepository.save(CommentLikeEntity
                .builder()
                .commentNo(commentNo)
                .userNo(userId)
                .build());
        log.info("[like] like");
        return true;
    }

    @Override
    public List<CommentDTO> findByWriterNo(Long writerNo) {
        return commentRepository.findByWriterNo(writerNo).stream().map(this::entityToDtoForMyPage).collect(Collectors.toList());
    }

    public CommentDTO entityToDtoForMyPage(CommentEntity commentEntity){
        log.info("[entityToDtoForMyPage] entityToDtoForMyPage start");
        CommentDTO commentDTO = CommentDTO.builder()
                .postNo(commentEntity.getPostNo())
                .commentNo(commentEntity.getCommentNo())
                .content(commentEntity.getContent())
                .CreatedAt(commentEntity.getCreatedAt())
                .likes(commentLikeRepository.countByCommentNo(commentEntity.getCommentNo()))
                .build();
        log.info("[entityToDtoForMyPage] entityToDtoForMyPage done");
        return commentDTO;
    }
}
