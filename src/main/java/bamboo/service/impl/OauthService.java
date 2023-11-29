package bamboo.service.impl;

import bamboo.dto.response.TokenDTO;
import bamboo.dto.response.UserCheckDTO;
import bamboo.dto.response.UserDTO;
import bamboo.entity.PeopleEntity;
import bamboo.entity.UserEntity;
import bamboo.exception.CustomException;
import bamboo.exception.ErrorCode;
import bamboo.repository.PeopleRepository;
import bamboo.repository.UserRepository;
import bamboo.util.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OauthService {

    private final PeopleRepository peopleRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Value("${url}")
    String url;
    @Value("${clientId}")
    String clientId;
    @Value("${clientSecret}")
    String clientSecret;
    @Value("${redirect}")
    String redirect;

    public String getAccessTokenFromGoogle(String accessCode){
        log.info("[getAccessTokenFromGoogle] getAccessTokenFromGoogle start");

        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> params = new HashMap<>();

        params.put("code",accessCode);
        params.put("client_id",clientId);
        params.put("client_secret",clientSecret);
        params.put("redirect_uri",redirect);
        params.put("grant_type","authorization_code");

        ResponseEntity<Map> response = restTemplate.postForEntity(url,params,Map.class);
        Map<String,String> map = response.getBody();
        log.info("[getAccessTokenFromGoogle] getAccessTokenFromGoogle done");
        return map.get("access_token");
    }

    public UserCheckDTO getUserInfoFromGoogle(String accessToken){
        log.info("[getUserNameFromGoogle] getUserNameFromGoogle start");

        String url = "https://www.googleapis.com/userinfo/v2/me?access_token="+accessToken;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.getForEntity(url,Map.class);
        Map<String,String> map = response.getBody();

        UserCheckDTO userCheckDTO = UserCheckDTO.builder()
                .email(map.get("email"))
                .name(map.get("name"))
                .build();
        log.info("[getUserNameFromGoogle] getUserNameFromGoogle done, userData : {}", userCheckDTO);
        return userCheckDTO;
    }

    public TokenDTO loginResult(UserCheckDTO userCheckDTO) throws CustomException {
        log.info("[loginResult] loginResult start");
        UserEntity user = checkUser(userCheckDTO);
        UserDTO userDTO = UserDTO.builder()
                .nickname(user.getNickname())
                .status(1)
                .role(user.getRole())
                .profileImg(user.getProfileImg())
                .build();

        log.info("[loginResult] loginResult done, role : {}", userDTO.getRole());
        return jwtTokenProvider.createToken(user);
    }

    public UserEntity checkUser(UserCheckDTO userCheckDTO) throws CustomException {
        log.info("[checkUser] checkUser start, userInfo : {}", userCheckDTO);
        PeopleEntity people = peopleRepository.findById(userCheckDTO.getName())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PEOPLE));
        if(people.getStatus() == 0){
            throw new CustomException(ErrorCode.NOT_FOUND_MEMBER,userCheckDTO);
        }

        UserEntity userEntity = userRepository.findByName(userCheckDTO.getName());
        if(!userEntity.getEmail().equals(userCheckDTO.getEmail())){
            throw new CustomException(ErrorCode.NOT_MATCHED_EMAIL);
        }
        log.info("[checkUser] checkUser done");
        return userEntity;
    }
}
