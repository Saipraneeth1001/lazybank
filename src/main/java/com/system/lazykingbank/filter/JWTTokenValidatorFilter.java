package com.system.lazykingbank.filter;

import com.system.lazykingbank.constants.Constants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JWTTokenValidatorFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String jwtToken = request.getHeader("Authorization");
        Environment env = getEnvironment();
        try {
            if (null != jwtToken) {
                String secret = env.getProperty("JWT_SECRET", Constants.JWT_SECRET);
                SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                if (null != secretKey) {
                    Claims claims = Jwts.parser().verifyWith(secretKey)
                            .build().parseSignedClaims(jwtToken).getPayload();
                    String username = String.valueOf(claims.get("username"));
                    String authorities = String.valueOf(claims.get("authorities"));
                    Authentication authentication = new UsernamePasswordAuthenticationToken
                            (username, null,
                                    AuthorityUtils
                                            .commaSeparatedStringToAuthorityList(authorities));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } else {
                throw new BadCredentialsException("bad token");
            }
        } catch(Exception e) {
            throw new BadCredentialsException("Invalid token");
        }
        filterChain.doFilter(request, response);

    }

    @Override
    public boolean shouldNotFilter(HttpServletRequest request) {
        return (
                request.getServletPath().contains("/register")
                        || request.getServletPath().contains("/signin")
                        || request.getServletPath().contains("/h2-console")
        );
    }
}
