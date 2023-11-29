package bamboo.service;

import bamboo.dto.request.RequestPostDTO;
import bamboo.dto.response.CommentDTO;
import bamboo.dto.response.PostDTO;
import bamboo.dto.response.User;
import bamboo.exception.CustomException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {

    Long addPost(User user, RequestPostDTO requestPostDTO) throws CustomException;

    String addImage(MultipartFile img) throws CustomException;

    PostDTO findByPostNo(Long userId, Long postNo) throws CustomException;

    List<PostDTO> findByCategory(int category) throws CustomException;

    Long putPost(RequestPostDTO requestPostDTO) throws CustomException;

    void deleteByPostNo(Long postNo);

    boolean like(Long userId, Long postNo);

    List<PostDTO> findByWriterNo(Long writerNo);
}
