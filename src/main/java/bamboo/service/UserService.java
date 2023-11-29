package bamboo.service;

import bamboo.dto.request.RequestUserDTO;
import bamboo.dto.response.TokenDTO;
import bamboo.dto.response.User;
import bamboo.dto.response.UserDTO;
import bamboo.exception.CustomException;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface UserService {
    TokenDTO googleLogin(String accessCode) throws CustomException;

    Boolean dupCheck(String name);

    TokenDTO signUp(RequestUserDTO requestUserDTO, MultipartFile multipartFile) throws IOException;

    UserDTO getUserInfo(User user);

    void putUserInfo(User user, RequestUserDTO requestUserDTO, MultipartFile profile) throws IOException;

    String newAccessToken(HttpServletRequest request);

    void logout(HttpServletRequest request);
}
