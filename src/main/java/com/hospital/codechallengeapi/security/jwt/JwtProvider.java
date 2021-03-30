package com.hospital.codechallengeapi.security.jwt;

import com.hospital.codechallengeapi.security.services.UserPrinciple;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
@Slf4j
public class JwtProvider {

  private final String jwtSecret;

  public JwtProvider(
      @Value("${jwt.secret:ee989a41-3f49-428f-841b-274b5a5a93cd}") String jwtSecret) {
    this.jwtSecret = jwtSecret;
  }

  public String generateJwtToken(Authentication authentication) {
    Instant expirationTime = Instant.now().plus(30, ChronoUnit.MINUTES);
    Date expirationDate = Date.from(expirationTime);
    UserPrinciple userPrincipal = (UserPrinciple) authentication.getPrincipal();
    Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

    String compactTokenString =
        Jwts.builder()
            .claim("sub", userPrincipal.getUsername())
            .setExpiration(expirationDate)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();

    return "Bearer " + compactTokenString;
  }

  public String getUserNameFromJwtToken(String token) {
    byte[] secretBytes = jwtSecret.getBytes();
    Jws<Claims> jwsClaims =
        Jwts.parserBuilder().setSigningKey(secretBytes).build().parseClaimsJws(token);
    return jwsClaims.getBody().getSubject();
  }

  public boolean validateJwtToken(String token) {

    try {
      byte[] secretBytes = jwtSecret.getBytes();
      Jwts.parserBuilder().setSigningKey(secretBytes).build().parseClaimsJws(token);
      return true;
    } catch (MalformedJwtException e) {
      log.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      log.error("JWT token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      log.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      log.error("JWT claims string is empty: {}", e.getMessage());
    }

    return false;
  }
}
