package br.ufsm.poli.csi.tapw.pilacoin.security;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class CookieUtil {

    private static final int MAX_AGE = 3600; // 1 hora

    public static void setCookie(String cookieName, String value, HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, value);
        cookie.setMaxAge(MAX_AGE);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public static Optional<String> getCookie(String cookieName, Cookie[] cookies) {
        if (cookies == null) {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
                .filter(c -> cookieName.equals(c.getName()))
                .map(Cookie::getValue)
                .findAny();
    }

    public static void removeCookie(String cookieName, HttpServletRequest request) {
        Objects.requireNonNull(Arrays.stream(request.getCookies())
                .filter(c -> cookieName.equals(c.getName()))
                .findAny().orElse(null)).setMaxAge(0);
    }

}
