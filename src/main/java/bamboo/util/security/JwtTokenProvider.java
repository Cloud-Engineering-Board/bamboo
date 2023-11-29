package bamboo.util.security;

import bamboo.dto.response.TokenDTO;
import bamboo.dto.response.UserDTO;
import bamboo.entity.TokenEntity;
import bamboo.entity.UserEntity;
import bamboo.exception.ErrorCode;
import bamboo.exception.TokenException;
import bamboo.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private String secretKey = "team3bamboo!@#$%team3bamboo!@#$%";
    private final long accessTokenLimit = 1000L*60*60*3;
    private final long refreshTokenLimit = 1000L*60*60*24*3;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;

    @PostConstruct
    protected void init(){
        log.info("[init] JwtTokenProvider secretKey init start");
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
        log.info("[init] JwtTokenProvider secretKey init end");
    }

    public TokenDTO createToken(UserEntity user){
        log.info("[createToken] create token start");
        Claims claims = Jwts.claims().setSubject(user.getName());
        claims.put("nickname", user.getNickname());
        claims.put("profile", user.getProfileImg());
        claims.put("role", user.getRole() == 1 ? "USER" : "ADMIN" );
        Date now = new Date();

        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenLimit))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenLimit))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        tokenRepository.save(TokenEntity.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiration(refreshTokenLimit)
                .build());

        log.info("[createToken] create token finish");

        return TokenDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public Authentication getAuthentication(String token){
        log.info("[getAuthentication] token authentication start");
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUsername(token));
        log.info("[getAuthentication] userName : {}, userAuthorities : {}",userDetails.getUsername(), userDetails.getAuthorities());
        return new UsernamePasswordAuthenticationToken(userDetails,"",userDetails.getAuthorities());
    }

    public String getUsername(String token){
        log.info("[getUsername] get name by token");
        String info = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
        log.info("[getUsername] info : {}", info);
        return info;
    }

    public UserDTO getUserInfo(String token){
        log.info("[getUserInfo] getUserInfo start");
        Claims info = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        UserDTO userDTO = UserDTO.builder()
                .nickname(info.get("nickname").toString())
                .profileImg(info.get("profile").toString())
                .role(info.get("role").equals("USER") ? 1 : 0)
                .build();
        log.info("[getUserInfo] getUserInfo done, user : {}", userDTO);
        return userDTO;
    }

    public TokenDTO resolveToken(HttpServletRequest request){
        log.info("[resolveToken] get token by header");
        return TokenDTO.builder()
                .refreshToken(request.getHeader("R-AUTH-TOKEN"))
                .accessToken(request.getHeader("X-AUTH-TOKEN"))
                .build();
    }

    public boolean validateToken(String token){
        log.info("[validateToken] token validate");
        try{
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        }catch(Exception e){
            log.info("[validateToken] error");
            return false;
        }
    }

    public boolean firstValidation(TokenDTO tokenDTO) throws TokenException {
        if(tokenDTO.getAccessToken() == null || tokenDTO.getRefreshToken() == null){
            throw new TokenException(ErrorCode.NOT_FOUND_TOKEN);
        }
        Optional<TokenEntity> optional = tokenRepository.findById(tokenDTO.getRefreshToken());
        if(optional.isPresent()){
            TokenEntity tokenEntity = optional.get();
            return tokenEntity.getAccessToken().equals(tokenDTO.getAccessToken());
        }
        throw new TokenException(ErrorCode.TOKEN_NOT_MATCHED);
    }

    public void deleteToken(String token){
        log.info("[deleteToken] deleteToken start");
        tokenRepository.deleteById(token);
        log.info("[deleteToken] deleteToken done");
    }

    public String validationRefreshToken(TokenDTO token){
        log.info("[validationRefreshToken] refreshToken validate start");
        Optional<TokenEntity> optional = tokenRepository.findById(token.getRefreshToken());

        if(optional.isEmpty()){
            log.info("[validationRefreshToken] optional is empty");
            return null;
        }

        TokenEntity tokenEntity = optional.get();
        if(tokenEntity.getAccessToken().equals(token.getAccessToken())){
            Claims info = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token.getRefreshToken()).getBody();

            Date now = new Date();
            String newAccessToken = Jwts.builder()
                    .setClaims(info)
                    .setIssuedAt(now)
                    .setExpiration(new Date(now.getTime() + accessTokenLimit))
                    .signWith(SignatureAlgorithm.HS256, secretKey)
                    .compact();

            tokenEntity.setAccessToken(newAccessToken);
            tokenRepository.save(tokenEntity);
            log.info("[validationRefreshToken] refreshToken validate done");
            return newAccessToken;
        }
        log.info("[validationRefreshToken] different token");
        return null;
    }
}
