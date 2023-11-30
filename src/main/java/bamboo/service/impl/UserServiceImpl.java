package bamboo.service.impl;

import bamboo.dto.request.RequestUserDTO;
import bamboo.dto.response.TokenDTO;
import bamboo.dto.response.User;
import bamboo.dto.response.UserCheckDTO;
import bamboo.dto.response.UserDTO;
import bamboo.entity.PeopleEntity;
import bamboo.entity.UserEntity;
import bamboo.exception.CustomException;
import bamboo.repository.PeopleRepository;
import bamboo.repository.UserRepository;
import bamboo.service.CommentService;
import bamboo.service.PostService;
import bamboo.service.UserService;
import bamboo.util.security.JwtTokenProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PeopleRepository peopleRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AmazonS3 amazonS3;
    private final OauthService oauthService;
    private final PostService postService;
    private final CommentService commentService;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    public TokenDTO googleLogin(String accessCode, String redirect) throws CustomException {
        log.info("[googleLogin] googleLogin start");
        String googleAccessToken = oauthService.getAccessTokenFromGoogle(accessCode, redirect);
        UserCheckDTO userCheckDTO = oauthService.getUserInfoFromGoogle(googleAccessToken);
        log.info("[googleLogin] googleLogin done");
        return oauthService.loginResult(userCheckDTO);
    }

    @Override
    public Boolean dupCheck(String nickname) {
        log.info("[dupCheck] dupCheck start");
        return userRepository.findByNickname(nickname).isEmpty();
    }

    @Override
    public TokenDTO signUp(RequestUserDTO requestUserDTO, MultipartFile profile) throws IOException {
        log.info("[signUp] signUp start");
        String imgUrl;

        if (profile == null) {
            imgUrl = amazonS3.getUrl(bucket + "/profile", "default").toString();
        } else {
            imgUrl = saveFile(profile, requestUserDTO.getNickname());
        }

        UserEntity userEntity = UserEntity.builder()
                .birth(requestUserDTO.getBirth())
                .role(1)
                .nickname(requestUserDTO.getNickname())
                .name(requestUserDTO.getName())
                .profileImg(imgUrl)
                .email(requestUserDTO.getEmail())
                .build();

        userRepository.save(userEntity);
        changePeopleStatus(requestUserDTO.getName());

        log.info("[signUp] signUp done, name : {}", requestUserDTO.getName());
        return jwtTokenProvider.createToken(userEntity);
    }

    public void changePeopleStatus(String name) {
        log.info("[changePeopleStatus] changePeopleStatus start, name : {}", name);
        PeopleEntity people = peopleRepository.findById(name).get();
        people.setStatus(1);
        peopleRepository.save(people);
        log.info("[changePeopleStatus] changePeopleStatus done");
    }

    public String saveFile(MultipartFile profile, String nickname) throws IOException {
        log.info("[saveFile] saveFile start, nickname : {}", nickname);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(profile.getSize());
        metadata.setContentType(profile.getContentType());

        amazonS3.putObject(bucket + "/profile", nickname, profile.getInputStream(), metadata);
        log.info("[saveFile] saveFile done");
        return amazonS3.getUrl(bucket + "/profile", nickname).toString();
    }

    @Override
    public UserDTO getUserInfo(User user) {
        UserEntity userEntity = userRepository.findById(user.getId()).get();
        UserDTO userDTO = UserDTO.builder()
                .nickname(userEntity.getNickname())
                .birth(userEntity.getBirth())
                .profileImg(userEntity.getProfileImg())
                .build();

        userDTO.setPostList(postService.findByWriterNo(user.getId()));
        userDTO.setCommentList(commentService.findByWriterNo(user.getId()));
        return userDTO;
    }

    @Override
    public void putUserInfo(User user, RequestUserDTO requestUserDTO, MultipartFile profile) throws IOException {
        log.info("[putUserInfo] putUserInfo start, user : {}", user);
        UserEntity userEntity = userRepository.findById(user.getId()).get();

        userEntity.setNickname(requestUserDTO.getNickname());
        if (profile == null) {
            userEntity.setProfileImg(amazonS3.getUrl(bucket + "/profile", "default").toString());
        } else {
            userEntity.setProfileImg(saveFile(profile, requestUserDTO.getNickname()));
        }

        userRepository.save(userEntity);

        log.info("[putUserInfo] putUserInfo done");
    }

    @Override
    public void logout(HttpServletRequest request) {
        log.info("[logout] logout start");
        jwtTokenProvider.deleteToken(request.getHeader("R-AUTH-TOKEN"));
        log.info("[logout] logout done");
    }

    @Override
    public String newAccessToken(HttpServletRequest request) {
        log.info("[newAccessToken] newAccessToken start");
        String newAccessToken = jwtTokenProvider.validationRefreshToken(TokenDTO.builder()
                .accessToken(request.getHeader("X-AUTH-TOKEN"))
                .refreshToken(request.getHeader("R-AUTH-TOKEN"))
                .build());
        log.info("[newAccessToken] newAccessToken done, newToken : {}", newAccessToken);
        return newAccessToken;
    }
}
