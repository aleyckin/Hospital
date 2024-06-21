package com.example.hospital.JWT;

import com.example.hospital.Hospital.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;

public class JwtFilter extends GenericFilterBean {
    private static final String AUTHORIZATION = "Authorization";
    public static final String TOKEN_BEGIN_STR = "Bearer ";

    private final UserService userService;

    public JwtFilter(UserService userService) {
        this.userService = userService;
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(bearer) && bearer.startsWith(TOKEN_BEGIN_STR)) {
            return bearer.substring(TOKEN_BEGIN_STR.length());
        }
        return null;
    }

    private void raiseException(ServletResponse response, int status, String message) throws IOException {
        if (response instanceof final HttpServletResponse httpResponse) {
            httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
            httpResponse.setStatus(status);
            final byte[] body = new ObjectMapper().writeValueAsBytes(message);
            response.getOutputStream().write(body);
        }
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        if (request instanceof final HttpServletRequest httpRequest) {
            final String token = getTokenFromRequest(httpRequest);
            if (StringUtils.hasText(token)) {
                try {
                    final UserDetails user = userService.loadUserByToken(token);
                    final UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } catch (JwtException e) {
                    raiseException(response, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    raiseException(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                            String.format("Internal error: %s", e.getMessage()));
                    return;
                }
            }
        }
        chain.doFilter(request, response);
    }
}
