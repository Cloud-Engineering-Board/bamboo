package bamboo.service.impl;

import bamboo.dto.response.User;
import bamboo.entity.UserEntity;
import bamboo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username){
        log.info("[loadUserByUsername] username : {}", username);
        UserEntity userEntity = userRepository.findByName(username);
        List<String> roles = new ArrayList<>();
        roles.add(userEntity.getRole() == 1 ? "USER" : "ADMIN");

        return User.builder().id(userEntity.getUserNo())
                .uid(Long.toString(userEntity.getUserNo()))
                .password(userEntity.getName())
                .name(userEntity.getNickname())
                .profile(userEntity.getProfileImg())
                .roles(roles)
                .build();
    }
}
