package com.shabnam.realtime_chat_app.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret:SuperSecretKeyForJWTAuthThatIsVeryLongAndSecure12345}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms:86400000}") // 24 hours
    private long jwtExpirationMs;

    public String generateToken(String username) {
        return JWT.create()
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .sign(Algorithm.HMAC512(jwtSecret.getBytes()));
    }

    public String getUsernameFromToken(String token) {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(jwtSecret.getBytes()))
                .build()
                .verify(token);
        return decodedJWT.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC512(jwtSecret.getBytes())).build();
            verifier.verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
