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

@Component
public class ApiAuthFilter extends OncePerRequestFilter{

    @Autowired
    private JwtUtility jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        //this filter should only be applied on the /posttime endpoint
        if(request.getMethod().equalsIgnoreCase("POST") && (request.getRequestURI().equals("/posttime"))) {
            String token = getToken(request);
            if(token != null && jwtUtil.validateToken(token)) {
                filterChain.doFilter(request, response);
                return;
            }
            else { //invalid credentials
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if(StringUtils.hasText(token) && token.startsWith("Bearer")) {
            return token.split(" ")[1].trim();
        }
        return null;
    }
    
}
