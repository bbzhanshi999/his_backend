package com.example.backend.sys.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class JwtUtils {

    private static String SECRECT;
    private static Long EXPIRE;
    private final static String BEARER = "Bearer ";

    /**
     * 生成jwt token
     *
     * @param subject
     * @param authorities
     * @return
     */
    public static String generateJWT(String subject, Collection<? extends GrantedAuthority> authorities) {
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(subject)
                .issuer("zql")
                .expirationTime(new Date(new Date().getTime() + EXPIRE))
                .claim("roles", authorities.stream().map(GrantedAuthority.class::cast)
                        .map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
                .build();
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

        try {
            signedJWT.sign(new MACSigner(SECRECT));
        } catch (JOSEException e) {
            e.printStackTrace();
        }
        return signedJWT.serialize();
    }

    public static void setSECRECT(String SECRECT) {
        JwtUtils.SECRECT = SECRECT;
    }

    /**
     * 转换成Authentication
     *
     * @param exchange
     * @return
     */
    public static Mono<Authentication> extract(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .flatMap(authValue -> Mono.justOrEmpty(authValue.substring(BEARER.length())))
                .flatMap(token -> {
                    try {
                        return Mono.justOrEmpty(SignedJWT.parse(token));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return Mono.empty();
                })
                .filter(signedJWT -> {
                    try {
                        return signedJWT.getJWTClaimsSet().getExpirationTime().after(Date.from(Instant.now()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return false;
                })
                .filter(signedJWT -> {
                    try {
                        return signedJWT.verify(new MACVerifier(SECRECT));
                    } catch (JOSEException e) {
                        e.printStackTrace();
                    }
                    return false;
                })
                .flatMap(signedJWT -> {
                    String subject;
                    String auths;
                    List<GrantedAuthority> authorities;

                    try {
                        subject = signedJWT.getJWTClaimsSet().getSubject();
                        auths = (String) signedJWT.getJWTClaimsSet().getClaim("roles");
                    } catch (ParseException e) {
                        return Mono.empty();
                    }
                    authorities = Stream.of(auths.split(","))
                            .filter(StringUtils::isNotBlank)
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    return Mono.justOrEmpty(new UsernamePasswordAuthenticationToken(subject, null, authorities));
                });
    }

    public static void setEXPIRE(Long expire) {
        EXPIRE=expire;
    }
}
