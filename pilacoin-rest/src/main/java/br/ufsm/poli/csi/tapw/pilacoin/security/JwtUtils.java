package br.ufsm.poli.csi.tapw.pilacoin.security;

import br.ufsm.poli.csi.tapw.pilacoin.model.Usuario;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtils {

    private static final long LIFETIME = Duration.ofSeconds(10 * 60).toMillis();
    private static final String SIGNATURE = "pilacoin";

    public String generateToken(Usuario usuario) {
        final Map<String, Object> claims = new HashMap<>();

        claims.put("sub", usuario.getEmail());
        claims.put("name", usuario.getNome());

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + LIFETIME))
                .signWith(SignatureAlgorithm.HS256, SIGNATURE)
                .compact();
    }

    public String getUsernameToken(String token) {
        return (token != null) ? parseToken(token).getSubject() : null;
    }

    public boolean isExpiredToken(String token) {
        return token != null && parseToken(token).getExpiration().before(new Date());
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(SIGNATURE)
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();
    }

}
