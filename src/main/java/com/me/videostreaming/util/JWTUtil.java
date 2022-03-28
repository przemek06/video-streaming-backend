package com.me.videostreaming.util;


import com.me.videostreaming.model.UserDetailsModel;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.*;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTUtil {

    private static final String secret= "asdasdasdfadsfadsfadsffdsafadsfadsfadsfasfadsfavcdas";

    private  static final Integer expirationTime=3600;

    private static Key key;

    public static Claims getAllClaimsFromToken(String token) {
        key= Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    private static String serializeUser(UserDetails user) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject( user );
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }


   public static UserDetails getUserFromToken(String token) throws IOException, ClassNotFoundException {
       Claims claims = getAllClaimsFromToken(token);
       UserDetailsModel obj;

       byte [] data = Base64.getDecoder().decode((String) claims.get("user"));
       ObjectInputStream ois = new ObjectInputStream(
               new ByteArrayInputStream(  data ) );
       obj  = (UserDetailsModel) ois.readObject();
       ois.close();
       return obj;
    }

    public static Date getExpirationDateFromToken(String token) {
        return getAllClaimsFromToken(token).getExpiration();
    }

    private static Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }


    public static String generateToken(UserDetails user) throws IOException {
        Map<String, Object> claims = new HashMap<>();
        String serialized = serializeUser(user);
        claims.put("user", serialized);
        return doGenerateToken(claims, user.getUsername());
    }

    private static String doGenerateToken(Map<String, Object> claims, String user) {
        key= Keys.hmacShaKeyFor(secret.getBytes());
        long expirationTimeLong = Integer.toUnsignedLong(expirationTime); //in second
        Date createdDate = new Date();
        Date expirationDate = new Date(createdDate.getTime() + expirationTimeLong * 1000);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(key)
                .compact();
    }

    public static Boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

}
