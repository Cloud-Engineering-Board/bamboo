package bamboo.controller;

import bamboo.dto.request.RequestUserDTO;
import bamboo.dto.response.TokenDTO;
import bamboo.dto.response.User;
import bamboo.dto.response.UserDTO;
import bamboo.exception.CustomException;
import bamboo.exception.ErrorDetails;
import bamboo.exception.TokenException;
import bamboo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("user")
@RequiredArgsConstructor
@Api(tags = "유저 api")
public class UserController {

    private final UserService userService;

    @GetMapping("/google")
    @ApiOperation(value = "google api 호출")
    public ResponseEntity<?> googleOauth(@RequestParam("code") String accessCode, @RequestParam("redirect") String redirect) throws CustomException {
        log.info("[googleOauth] googleOauth start");
        TokenDTO token = userService.googleLogin(accessCode, redirect);
        log.info("[googleOauth] googleOauth done");
        return new ResponseEntity<>(token,HttpStatus.OK);
    }

    @GetMapping("/duplicate")
    @ApiOperation(value = "닉네임 중복 확인")
    ResponseEntity<?> dupCheck(@RequestParam("nickname") String nickname){
        log.info("[dupCheck] dupCheck start");
        Boolean result = userService.dupCheck(nickname);
        log.info("[dupCheck] dupCheck done");
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    @PostMapping
    @ApiOperation(value = "회원 가입")
    public ResponseEntity<?> signUp(@RequestParam("user") String requestUserDTO,
                                    @RequestPart(required = false) MultipartFile profile) throws IOException {
        log.info("[signUp] signUp start, requestUserDTO : {}", requestUserDTO);
        ObjectMapper objectMapper = new ObjectMapper();
        RequestUserDTO userDTO = objectMapper.readValue(requestUserDTO,RequestUserDTO.class);
        TokenDTO token = userService.signUp(userDTO, profile);
        log.info("[signUp] signUp done");
        return new ResponseEntity<>(token,HttpStatus.OK);
    }

    @GetMapping
    @ApiOperation(value = "유저 정보 조회")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal User user){
        log.info("[getUserInfo] getUserInfo start");
        UserDTO userDTO = userService.getUserInfo(user);
        log.info("[getUserInfo] getUserInfo done");
        return new ResponseEntity<>(userDTO,HttpStatus.OK);
    }

    @PutMapping
    @ApiOperation(value = "유저 정보 수정")
    public ResponseEntity<?> putUserInfo(@AuthenticationPrincipal User user,
                                         @RequestParam("user") String requestUserDTO,
                                         @RequestPart(required = false) MultipartFile profile) throws IOException {
        log.info("[putUserInfo] putUserInfo start, requestUserDTO : {}", requestUserDTO);
        ObjectMapper objectMapper = new ObjectMapper();
        RequestUserDTO userDTO = objectMapper.readValue(requestUserDTO,RequestUserDTO.class);
        userService.putUserInfo(user,userDTO, profile);
        log.info("[putUserInfo] putUserInfo done");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping
    @ApiOperation(value = "로그아웃")
    public ResponseEntity<?> logout(HttpServletRequest request){
        log.info("[logout] logout start");
        userService.logout(request);
        log.info("[logout] logout done");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/token")
    @ApiOperation(value = "토큰 재발급")
    public ResponseEntity<?> newAccessToken(HttpServletRequest request){
        log.info("[newAccessToken] newAccessToken start");
        String newAccessToken = userService.newAccessToken(request);
        log.info("[newAccessToken] newAccessToken done");
        return new ResponseEntity<>(newAccessToken,HttpStatus.OK);
    }

    @GetMapping("/test")
    @ApiOperation(value = "토큰 재발급")
    public ResponseEntity<?> test(){
        return new ResponseEntity<>("너무귀찮구요",HttpStatus.OK);
    }
    
    @ExceptionHandler
    public ResponseEntity<?> Exception(CustomException e){
        log.info("[Exception] CustomException error");
        return new ResponseEntity<>(ErrorDetails.builder()
                .code(e.getErrorCode().getCode())
                .message(e.getErrorCode().getMessage())
                .userCheckDTO(e.getUserCheckDTO())
                .build(),
                HttpStatus.BAD_REQUEST);
    }

}
