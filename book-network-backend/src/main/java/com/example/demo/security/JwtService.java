package com.example.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    public String generateToken(UserDetails userDetails){

        return generateToken(new HashMap<>(),userDetails);
    }

    public  String generateToken(Map<String,Object> claims, UserDetails userDetails) {
        long jwtExpiration = 8640000;
        return buildToken(claims,userDetails, jwtExpiration);
    }
    private String buildToken(Map<String,Object> extraClaims,UserDetails userDetails,long jwtExpiration){
        var authorities = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+jwtExpiration))
                .claim("authorities",authorities)
                .signWith(getSignInKey())
                .compact();
    }
    public boolean isTokenValid(String token,UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }
    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }
    public <T> T extractClaim(String token, Function<Claims,T> claimResolver){
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }
    private Claims extractAllClaims(String token){
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    private SecretKey getSignInKey() {
        String secretKey = "66698983372e068e89e9195ab35b0f843fd5c02dfe858de38cc8656ab36c792eb3fa96517d87d2400609ee58d359d1a0a312d5bbfc79805ed92c4bba843affe693bf71152e794f3f20e41b7a2df67b78890be026601e1247751db71bab2fedefe8ee04ec8c7e370ccf5d1177f4ef87d91fa12ebf1b29b8db6cd858cebeda4d6d5dd06bc84efae6a754c9b8c029b1959fd9afbb2c9a5f436bfab8e74f6eba0a7f9c065f7cf3f86dc0f330b4e0c45e72a2f67b653356ca41a1f7f865823da5df1a5c12006051e6af10e1a20a63a66e80fe611e8df907c1d69c63f693c7e4a383b3296177cc27074473ede8c43d459c50cfaa9028f37d11561d50ed357bfc1c00cc";
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
