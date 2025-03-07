package org.ukdw.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.ukdw.dto.response.ErrorResponse;
import org.ukdw.services.JwtService;
import org.ukdw.services.UserAccountService;

import java.io.IOException;
import java.time.Instant;

import static org.apache.http.HttpHeaders.AUTHORIZATION;

/**
 * Creator: dendy
 * Date: 6/30/2020
 * Time: 11:59 AM
 * Description :  Contains the logic to call the token validation
 */

@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    //    private final AuthService authService;
    private final JwtService jwtService;
    //    private final AppProperties appProperties;
    private final UserAccountService userAccountService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException, JwtException {
        final String token;
        final String username;
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        token = authHeader.substring(7);
        try {
            username = jwtService.extractUserName(token);
        } catch (JwtException e) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.UNAUTHORIZED.value(),
                    "JwtException",
                    e.getMessage(),
                    Instant.now().toString()
            );
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(convertToJson(errorResponse));  // Convert ErrorResponse to JSON
            return;  // Exit early to prevent further processing
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userAccountService.userDetailsService()
                    .loadUserByUsername(username);
            if (jwtService.isTokenValid(token, userDetails)) {
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                securityContext.setAuthentication(authToken);
                SecurityContextHolder.setContext(securityContext);
//                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }

//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
//        // Populate excludeUrlPatterns on which one to exclude here
//        AntPathMatcher pathMatcher = new AntPathMatcher();
//        return appProperties.getExcludeFilter().stream()
//                .anyMatch(p -> pathMatcher.match(p, request.getServletPath()));
//    }

    // Method to convert the ErrorResponse to JSON
    private String convertToJson(Object object) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return "{}";  // Return empty JSON object in case of error
        }
    }
}