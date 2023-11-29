package bamboo.controller;

import bamboo.dto.request.RequestCommentDTO;
import bamboo.dto.response.CommentDTO;
import bamboo.dto.response.User;
import bamboo.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
@Api(tags = "댓글 api")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @ApiOperation(value = "댓글 작성", notes = "댓글 작성")
    public ResponseEntity<?> addComment(@AuthenticationPrincipal User user, @RequestBody RequestCommentDTO requestCommentDTO){
        log.info("[addComment] addComment start");
        Long result = commentService.addComment(user, requestCommentDTO);
        log.info("[addComment] addComment done");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping
    @ApiOperation(value = "댓글 조회")
    public ResponseEntity<?> getComment(@AuthenticationPrincipal User user, @RequestParam Long postNo){
        log.info("[getComment] getComment start, postNo : {}", postNo);
        List<CommentDTO> list = commentService.getComment(user, postNo);
        log.info("[getComment] getComment done");
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PutMapping
    @ApiOperation(value = "댓글 수정")
    public ResponseEntity<?> putComment(@RequestBody RequestCommentDTO requestCommentDTO){
        log.info("[putComment] putComment start, commentNo : {}", requestCommentDTO.getCommentNo());
        Long result = commentService.putComment(requestCommentDTO);
        log.info("[putComment] putComment done");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping
    @ApiOperation(value = "댓글 삭제")
    public ResponseEntity<?> deleteComment(@RequestParam Long commentNo){
        log.info("[deleteComment] deleteComment start, commentNo : {}", commentNo);
        boolean result = commentService.deleteComment(commentNo);
        log.info("[deleteComment] deleteComment done");
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    @PostMapping("/like/{commentNo}")
    @ApiOperation(value = "댓글 좋아요")
    public ResponseEntity<?> likeComment(@AuthenticationPrincipal User user, @PathVariable Long commentNo){
        log.info("[likeComment] likeComment start");
        boolean result = commentService.like(user.getId(), commentNo);
        log.info("[likeComment] likeComment done");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
