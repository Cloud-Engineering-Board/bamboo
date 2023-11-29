package bamboo.service;


import bamboo.dto.request.RequestCommentDTO;
import bamboo.dto.response.CommentDTO;
import bamboo.dto.response.User;

import java.util.List;

public interface CommentService {

    Long addComment(User user, RequestCommentDTO requestCommentDTO);

    List<CommentDTO> getComment(User user, Long postNo);

    Long putComment(RequestCommentDTO requestCommentDTO);

    boolean deleteComment(Long commentNo);

    boolean like(Long userId, Long postNo);

    List<CommentDTO> findByWriterNo(Long writerNo);
}
