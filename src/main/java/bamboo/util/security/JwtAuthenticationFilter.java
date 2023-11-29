package bamboo.util.security;

import bamboo.dto.response.TokenDTO;
import bamboo.exception.ErrorCode;
import bamboo.exception.TokenException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest servletRequest,
                                    HttpServletResponse servletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        TokenDTO token = jwtTokenProvider.resolveToken(servletRequest);
        log.info("[doFilterInternal] token : {} ", token);

        log.info("[doFilterInternal] token validation start ");
        try {
            if (jwtTokenProvider.firstValidation(token)) {
                if (jwtTokenProvider.validateToken(token.getAccessToken())) {
                    Authentication authentication = jwtTokenProvider.getAuthentication(token.getAccessToken());
                    log.info("[doFilterInternal] Authentication : {}", authentication);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("[doFilterInternal] token validation done");
                } else {
                    throw new TokenException(ErrorCode.ACCESS_TOKEN_EXPIRED);
                }
            } else {
                throw new TokenException(ErrorCode.TOKEN_NOT_MATCHED);
            }
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (TokenException e) {
            setErrorResponse(servletResponse, e.getErrorCode());
        }
    }

    private void setErrorResponse(
            HttpServletResponse response,
            ErrorCode errorCode
    ) {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(errorCode.getCode());
        response.setCharacterEncoding("utf-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try {
            response.getWriter().write(objectMapper.writeValueAsString(errorCode+" "+errorCode.getMessage()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
