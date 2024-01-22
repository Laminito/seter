package sn.setter.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import sn.setter.utils.Constantes

import java.nio.charset.StandardCharsets
import java.util.Base64

import java.util.function.Function

@Service
class JwtUtil {

 /*   @Value("${application.security.jwt.secret-secretKey}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;*/

      private String expirationString;
      private String secret = Constantes.SECRET_KEY
      private Long expiration = 3600;


    public String generateToken(String userId,String username) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiration * 1000);

        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userId,username, now, expirationDate);
    }

    private String createToken(Map<String, Object> claims, String id,String subject, Date issuedAt, Date expirationDate) {
        return Jwts.builder()
                .setClaims(claims)
                .setId(id)
                .setSubject(subject)
                .setIssuedAt(issuedAt)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    String getExpirationString() {
        return expirationString
    }

    String getSecret() {
        return secret
    }

    Long getExpiration() {
        return expiration
    }

    void setExpiration(Long expiration) {
        this.expiration = expiration
    }
}
