package bamboo.controller;

import bamboo.dto.request.RequestPostDTO;
import bamboo.dto.response.PostDTO;
import bamboo.dto.response.User;
import bamboo.exception.CustomException;
import bamboo.service.PostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
@Api(tags = "게시글 api")
public class PostController {

    private final PostService postService;

    @PostMapping
    @ApiOperation("게시글 작성")
    public ResponseEntity<?> addPost(@AuthenticationPrincipal User user, @RequestBody RequestPostDTO requestPostDTO) throws CustomException {
        log.info("[addPost] addPost start");
        Long postNo = postService.addPost(user, requestPostDTO);
        log.info("[addPost] addPost done");
        return new ResponseEntity<>(postNo,HttpStatus.OK);
    }

    @PostMapping("/img")
    @ApiOperation("S3 이미지 등록")
    public ResponseEntity<?> addImage(@RequestPart MultipartFile img) throws CustomException{
        log.info("[addImage] addImage start");
        String url = postService.addImage(img);
        log.info("[addImage] addImage done");
        return new ResponseEntity<>(url,HttpStatus.OK);
    }

    @GetMapping
    @ApiOperation(value = "특정 게시글 조회")
    public ResponseEntity<?> findByPostNo(@AuthenticationPrincipal User user, @RequestParam Long postNo) throws CustomException{
        log.info("[findByPostNo] findByPostNo start, postNo : {}", postNo);
        PostDTO postDTO = postService.findByPostNo(user.getId(), postNo);
        log.info("[findByPostNo] findByPostNo done");
        return new ResponseEntity<>(postDTO,HttpStatus.OK);
    }

    @GetMapping("/{category}")
    @ApiOperation(value = "카테고리별 게시글 조회", notes = "0 : 공지사항, 1 : 잡담, 2 : 정보공유, 3 : 번개")
    public ResponseEntity<?> findByCategory(@PathVariable int category) throws CustomException{
        log.info("[findByCategory] findByCategory start");
        List<PostDTO> list = postService.findByCategory(category);
        log.info("[findByCategory] findByCategory done");
        return new ResponseEntity<>(list,HttpStatus.OK);
    }

    @PutMapping
    @ApiOperation(value = "게시글 수정")
    public ResponseEntity<?> putPost(@RequestBody RequestPostDTO requestPostDTO) throws CustomException {
        log.info("[putPost] putPost start");
        Long postNo = postService.putPost(requestPostDTO);
        log.info("[putPost] putPost done");
        return new ResponseEntity<>(postNo,HttpStatus.OK);
    }

    @DeleteMapping
    @ApiOperation(value = "게시글 삭제")
    public ResponseEntity<?> deleteByPostNo(@RequestParam Long postNo){
        log.info("[deleteByPostNo] deleteByPostNo start");
        postService.deleteByPostNo(postNo);
        log.info("[deleteByPostNo] deleteByPostNo done");
        return new ResponseEntity<>("삭제 완료", HttpStatus.OK);
    }

    @PostMapping("like/{postNo}")
    @ApiOperation(value = "게시글 좋아요")
    public ResponseEntity<?> likePost(@AuthenticationPrincipal User user, @PathVariable Long postNo){
        log.info("[likePost] likePost start");
        boolean result = postService.like(user.getId(), postNo);
        log.info("[likePost] likePost done");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ExceptionHandler
    public ResponseEntity<?> handle(CustomException customException){
        log.error(customException.getMessage());
        return new ResponseEntity<>("에러 발생했대요",HttpStatus.OK);
    }
}
