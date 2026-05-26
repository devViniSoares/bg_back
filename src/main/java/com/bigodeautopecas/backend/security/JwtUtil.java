package com.bigodeautopecas.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String CLAIM_TIPO = "tipo";
    private static final String TOKEN_TYPE_REFRESH = "REFRESH";

    private final SecretKey secretKey;
    private final long expiration;
    private final long refreshExpiration;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration,
            @Value("${jwt.refresh-expiration:604800000}") long refreshExpiration) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
        this.refreshExpiration = refreshExpiration;
    }

    /** Gera access token com role do usuário (expira em 24h por padrão). */
    public String gerarToken(String email, String tipo) {
        return Jwts.builder()
                .subject(email)
                .claim(CLAIM_TIPO, tipo)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey)
                .compact();
    }

    /** Gera refresh token (expira em 7 dias por padrão) — não contém role. */
    public String gerarRefreshToken(String email) {
        return Jwts.builder()
                .subject(email)
                .claim(CLAIM_TIPO, TOKEN_TYPE_REFRESH)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(secretKey)
                .compact();
    }

    public String extrairEmail(String token) {
        return parsear(token).getPayload().getSubject();
    }

    public String extrairTipo(String token) {
        return parsear(token).getPayload().get(CLAIM_TIPO, String.class);
    }

    public boolean isRefreshToken(String token) {
        try {
            return TOKEN_TYPE_REFRESH.equals(extrairTipo(token));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validar(String token) {
        try {
            parsear(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Jws<Claims> parsear(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
    }
}