package com.project.BRP_backend.service.security.jwt;

import com.project.BRP_backend.enums.security.TokenType;
import com.project.BRP_backend.model.security.Token;
import com.project.BRP_backend.model.security.TokenData;
import com.project.BRP_backend.model.user.User;
import com.project.BRP_backend.service.user.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.TriConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.project.BRP_backend.constant.security.Constant.*;
import static com.project.BRP_backend.enums.security.TokenType.*;
import static org.springframework.boot.web.server.Cookie.SameSite.NONE;

@Service
@Getter
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.secret}")
    private String secret;

    private final UserService userService;

    private final Supplier<SecretKey> keySupplier = () -> Keys
            .hmacShaKeyFor(
                    Decoders.BASE64.decode(getSecret())
            );
    private final Function<String, Claims> claimsFunction = token -> Jwts.parser()
            .verifyWith(keySupplier.get())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    private final Function<String, String> getSubject = token -> getClaimsValue(token, Claims::getSubject);

    /*
    * Extracts the token from the HttpRequest and looks for the cookie with the given name
    *
    * */

    private final BiFunction<HttpServletRequest, String, Optional<String>> extractTokenFromRequest = (request, cookieName) ->
            Optional.of(Arrays.stream(request.getCookies() == null ?
                    new Cookie[]{new Cookie(EMPTY_VALUE, EMPTY_VALUE)}
                    :
                    request.getCookies())
                    .filter(cookie -> Objects.equals(cookieName, cookie.getName()))
                    .map(Cookie::getValue)
                    .findAny()
            ).orElse(Optional.empty());

    private final BiFunction<HttpServletRequest, String, Optional<Cookie>> extractCookieFromRequest = (request, cookieName) ->
            Optional.of(Arrays.stream(request.getCookies() == null ?
                            new Cookie[]{new Cookie(EMPTY_VALUE, EMPTY_VALUE)}
                            :
                            request.getCookies())
                    .filter(cookie -> Objects.equals(cookieName, cookie.getName()))
                    .findAny()
            ).orElse(Optional.empty());
    private final Supplier<JwtBuilder> jwtBuilderSupplier = () ->
            Jwts.builder()
                    .header().add(Map.of(TYPE, JWT_TYPE))
                    .and()
                    .audience().add("BRP_BACKEND")
                    .and()
                    .id(String.valueOf(UUID.randomUUID()))
                    .issuedAt(Date.from(Instant.now()))
                    .notBefore(new Date())
                    .signWith(keySupplier.get(), Jwts.SIG.HS512);

    private final BiFunction<User, TokenType, String> buildToken = (user, tokenType) ->
            Objects.equals(tokenType, ACCESS) ? jwtBuilderSupplier.get()
                    .subject(user.getUserId())
                    .claim(ROLE, user.getRole())
                    .expiration(Date.from(Instant.now().plusSeconds(getExpiration())))
                    .compact() :
                    jwtBuilderSupplier.get()
                            .subject(user.getEmail())
                            .compact();

    private final TriConsumer<HttpServletResponse, User, TokenType> addCookieToResponse = (response, user, tokenType) -> {
        switch (tokenType) {
            case ACCESS -> {
                var accessToken = createToken(user, Token::getAccess);
                var cookie = new Cookie(tokenType.getValue(), accessToken);
                cookie.setHttpOnly(true);
                //cookie.setSecure(true); //For Https
                cookie.setMaxAge(2*60*60);
                cookie.setPath("/");
                cookie.setAttribute("SameSite", NONE.name());
                response.addCookie(cookie);
            }
            case REFRESH -> {
                var refreshToken = createToken(user, Token::getRefresh);
                var cookie = new Cookie(tokenType.getValue(), refreshToken);
                cookie.setHttpOnly(true);
                // cookie.setSecure(true);
                cookie.setMaxAge(60 * 60 * 60);
                cookie.setPath("/");
                cookie.setAttribute("SameSite", NONE.name());
                response.addCookie(cookie);
            }
        }
    };

    private final BiConsumer<HttpServletResponse, User> addHeader = (response, user) -> {
        var accessToken = createToken(user, Token::getAccess);
        response.addHeader("Authorization", String.format("Bearer %s",accessToken));
    };


    @Override
    public String createToken(User user, Function<Token, String> tokenFunction) {
        var token = Token.builder()
                .access(buildToken.apply(user,ACCESS))
                .refresh(buildToken.apply(user, REFRESH))
                .build();
        return tokenFunction.apply(token);
    }

    @Override
    public Optional<String> extractToken(HttpServletRequest httpServletRequest, String cookieName) {
        return extractTokenFromRequest.apply(httpServletRequest,cookieName);
    }

    @Override
    public void addCookie(HttpServletResponse response, User user, TokenType tokenType) {
        addCookieToResponse.accept(response, user, tokenType);
    }

    @Override
    public void addHeader(HttpServletResponse response, User user) {
        addHeader.accept(response,user);
    }

    @Override
    public <T> T getTokenData(String token, Function<TokenData, T> tokenDataTFunction) {

        User user = userService.getUserByUserId(getSubject.apply(token));
        return tokenDataTFunction.apply(
                TokenData.builder()
                        .valid(Objects.equals(user.getUserId(), claimsFunction.apply(token).getSubject())
                        )
                        .expired(Instant.now().isAfter(getClaimsValue(token,Claims::getExpiration).toInstant()))
                        .grantedAuthorities(List.of(new SimpleGrantedAuthority((String) claimsFunction.apply(token).get(ROLE))))
                        .claims(claimsFunction.apply(token))
                        .user((UserDetails) user)
                        .build()
        );
    }

    @Override
    public <T> T getClaimsValue(String token, Function<Claims, T> claimsTFunction) {
        return claimsFunction.andThen(claimsTFunction).apply(token);
    }

    @Override
    public void removeCookie(HttpServletRequest request, HttpServletResponse response, String cookieName) {
        var optionalCookie = extractCookieFromRequest.apply(request,cookieName);
        if (optionalCookie.isPresent()){
            var cookie = optionalCookie.get();
            cookie.setMaxAge(0);//Invalidates the cookie
            response.addCookie(cookie);
        }
    }

    @Override
    public boolean validateToken(HttpServletRequest request) {
        var token = extractTokenFromRequest.apply(request, ACCESS.getValue());
        if (token.isPresent()) {
            return getTokenData(token.get(), TokenData::isValid);
        }
        return false;
    }
}
