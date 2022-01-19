package security;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil implements Serializable {

  private static final long serialVersionUID = 1L;

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.validation}")
  private long validation;

  public String generateToken(String subject, UserProxy userProxy) {
    Claims claims = Jwts.claims().setSubject(subject);
    claims.put("id", userProxy.userId);
    claims.put("type", userProxy.type);
    claims.put("sessionId", userProxy.sessionId);
    // We don't know why we are setting scopes like this
    claims.put("scopes", Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")));
    return Jwts.builder().setClaims(claims).setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + validation * 1000))
        .signWith(SignatureAlgorithm.HS256, secret).compact();
  }

  public UserProxy validateToken(String token) {
    Claims body = null;
    try {
      body = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    } catch (Exception e) {
      return null;
    }

    if (body.getExpiration().before(new Date())) {
      return null;
    }
    Long id = body.get("id", Long.class);
    String type = body.get("type", String.class);
    String sessionId = body.get("sessionId", String.class);
    return new UserProxy(type, id, sessionId);
  }
}
