package com.pdf.backend.security.jwt;

import com.pdf.backend.entities.User;
import com.pdf.backend.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${bezkoder.app.jwtSecret}")
    private String jwtSecret;

    private String dobsJwtSecret = "aaaaaaaaaaaaaaaaaaaaaaaa";

    @Value("${bezkoder.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    public String generateJwtToken(Authentication authentication) {

        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userPrincipal.getId().toString());
        claims.put("username", userPrincipal.getName());
        String roleString = "";
        List<String> roleList = userPrincipal.getAuthorities().stream()
                .filter(role -> !ObjectUtils.isEmpty(role.getAuthority()))
                .map(role -> role.getAuthority())
                .collect(Collectors.toList());

        claims.put("roles", roleList);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String generateDobsJwtToken(String username) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, dobsJwtSecret)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public String getUserNameFromJwtTokenDOBS(String token) {
        return Jwts.parser().setSigningKey(dobsJwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    public Map<String, Object> getClaims(String authToken) {
        Map<String, Object> claims = Jwts.parser()
                .setSigningKey(dobsJwtSecret)
                .parseClaimsJws(authToken)
                .getBody();

        return claims;
    }

    public Map<String, Object> getClaimsFromJwtToken(HttpServletRequest request) {
        String authToken = parseJwt(request);
        if (!ObjectUtils.isEmpty(authToken) && validateJwtToken(authToken)) {
            return getClaims(authToken);
        }
        return new HashMap<>();
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7, headerAuth.length());
        }

        return null;
    }

    public User getUserFromJwtToken(HttpServletRequest request) {
        Map<String, Object> claims = getClaimsFromJwtToken(request);
        try {
            User user = new User();
            if (claims.containsKey("userId") && claims.containsKey("username") && !ObjectUtils.isEmpty(claims.get("userId"))) {
                Long userID = Long.valueOf((String) claims.get("userId"));
                String userName = (String) claims.get("username");
                user.setId(userID);
                user.setUsername(userName);
                user.setName(userName);
            }
            return user;
        } catch (Exception e) {
            e.getStackTrace();
        }
        return null;
    }

    public Long getUserIdFromJwtToken(HttpServletRequest request) {
        Map<String, Object> claims = getClaimsFromJwtToken(request);
        if (claims.containsKey("userId") && !ObjectUtils.isEmpty(claims.get("userId"))) {
            return Long.valueOf((String) claims.get("userId"));
        }
        return null;
    }

    public List<?> getUserRoleFromJwtToken(HttpServletRequest request) {
        Map<String, Object> claims = getClaimsFromJwtToken(request);
        if (claims.containsKey("roles")) {
            List<?> rolesList = (List<?>) claims.get("roles");
            return rolesList;
        }
        return new LinkedList<>();
    }
}
