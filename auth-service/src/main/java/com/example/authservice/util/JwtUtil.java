package com.example.authservice.util;

import com.example.authservice.dto.AuthRequest;
import com.example.authservice.exception.AuthenticationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${jwt.secret}")
    String jwtSecretKey;

    @Value("${jwt.expiration.access}")
    Long jwtExpirationMs;

    @Value("${jwt.expiration.refresh}")
    Long refreshTokenExpirationMs;

    public String generateAccessToken(AuthRequest authReq) {
        return generateToken(jwtExpirationMs, authReq);
    }

    public String generateRefreshToken(AuthRequest authReq) {
        return generateToken(refreshTokenExpirationMs, authReq);
    }

    private String generateToken(Long expiration, AuthRequest authReq) {
        return Jwts
                .builder()
                .subject(authReq.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    public boolean isTokenValid(String token, AuthRequest authReq) {
        return (extractUsername(token).equals(authReq.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return getTokenExpirationDate(token).before(new Date());
    }

    private Date getTokenExpirationDate(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts
                    .parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (SignatureException e) {
            throw new AuthenticationException(e.getMessage());
        }
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
