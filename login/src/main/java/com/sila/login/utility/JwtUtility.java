package com.sila.login.utility;

import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

@Component
public class JwtUtility {

    private String secret = "secretkey";

    private long exp = 2* 3600* 1000; // 2 hours

    public String generateToken(String username) {
        return Jwts.builder().setSubject(username).setIssuedAt(new Date(System.currentTimeMillis())).
        setExpiration(new Date(System.currentTimeMillis()+exp))
        .signWith(SignatureAlgorithm.HS512, secret)
        .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        }
        catch(ExpiredJwtException e) {
            System.out.println(e.getMessage());
        }
        catch(IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
        catch(MalformedJwtException e) {
            System.out.println(e.getMessage());
        }
        catch(SignatureException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
}
