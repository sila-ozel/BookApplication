package com.sila.login.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sila.login.utility.JwtUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ApiAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(ApiAuthFilter.class);

    @Autowired
    private JwtUtility jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (isSecuredEndpoint(request)) {
            String token = extractToken(request);

            if (token != null && jwtUtil.validateToken(token)) {
                filterChain.doFilter(request, response);
                return;
            } else {
                logger.warn("Unauthorized access attempt to secured endpoint: {}", request.getRequestURI());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isSecuredEndpoint(HttpServletRequest request) {
        return "POST".equalsIgnoreCase(request.getMethod())
                && "/posttime".equalsIgnoreCase(request.getRequestURI());
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String[] parts = header.split(" ");
            if (parts.length == 2) {
                return parts[1].trim();
            }
        }
        return null;
    }
}
