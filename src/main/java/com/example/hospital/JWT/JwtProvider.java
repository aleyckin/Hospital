package com.example.hospital.JWT;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Component
public class JwtProvider{
    private final static Logger LOG = LoggerFactory.getLogger(JwtProvider.class);

    private final static byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);
    private final static String ISSUER = "auth0";

    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public JwtProvider(JwtProperties jwtProperties) {
        if (!jwtProperties.isDev()) {
            LOG.info("Generate new JWT key for prod");
            try {
                final MessageDigest salt = MessageDigest.getInstance("SHA-256");
                salt.update(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
                LOG.info("Use generated JWT key for prod \n{}", bytesToHex(salt.digest()));
                algorithm = Algorithm.HMAC256(bytesToHex(salt.digest()));
            } catch (NoSuchAlgorithmException e) {
                throw new JwtException(e);
            }
        } else {
            LOG.info("Use default JWT key for dev \n{}", jwtProperties.getDevToken());
            algorithm = Algorithm.HMAC256(jwtProperties.getDevToken());
        }
        verifier = JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build();
    }

    private static String bytesToHex(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }

    public String generateToken(String login) {
        final Date issueDate = Date.from(LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant());
        final Date expireDate = Date.from(LocalDate.now()
                .plusDays(15)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant());
        var temp = JWT.create();
        var temp2 = temp.withIssuer(ISSUER);
        var temp3 = temp2.withIssuedAt(issueDate);
        var temp4 = temp3.withExpiresAt(expireDate);
        var temp5 = temp4.withSubject(login);
        var temp6 = temp5.sign(algorithm);
        return temp6;
    }

    private DecodedJWT validateToken(String token) {
        try {
            return verifier.verify(token);
        } catch (JWTVerificationException e) {
            throw new JwtException(String.format("Token verification error: %s", e.getMessage()));
        }
    }

    public boolean isTokenValid(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }
        try {
            validateToken(token);
            return true;
        } catch (JwtException e) {
            LOG.error(e.getMessage());
            return false;
        }
    }

    public Optional<String> getLoginFromToken(String token) {
        try {
            return Optional.ofNullable(validateToken(token).getSubject());
        } catch (JwtException e) {
            LOG.error(e.getMessage());
            return Optional.empty();
        }
    }
}
