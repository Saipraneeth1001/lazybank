package com.system.lazykingbank.filter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

import static com.system.lazykingbank.constants.Constants.JWT_SECRET;

public class JWTTokenGeneratorFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (null != authentication) {
            Environment env = getEnvironment();
            if (null != env) {
                String secret = env.getProperty("JWT_SECRET", JWT_SECRET);
                SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                String jwt = Jwts.builder().issuer("Lazy Bank")
                        .subject("JWT_TOKEN")
                        .claim("username", authentication.getName())
                        .claim("authorities",
                                authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                                        .collect(Collectors.joining(","))
                        ).issuedAt(new Date()).expiration(new Date(new Date().getTime() + 30000))
                        .signWith(secretKey).compact();
                response.setHeader("Authorization", jwt);
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    public boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().contains("/register")
                || request.getServletPath().contains("h2");
    }
}
