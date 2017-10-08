package jp.classmethod.spring_stateless_csrf_filter.session;

import jp.classmethod.spring_stateless_csrf_filter.token.TokenSigner;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public class CookieSessionProvider implements SessionProvider<CookieSession> {

    private final SessionCookieBaker baker;
    private final TokenSigner signer;

    public CookieSessionProvider(SessionCookieBaker baker, TokenSigner signer){
        this.baker = baker;
        this.signer = signer;
    }

    @Override
    public Optional<CookieSession> get(HttpServletRequest request, boolean create) {
        final Cookie[] cookies = request.getCookies();
        final String cookieName = baker.getCookieName();
        for(Cookie cookie: cookies){
            if(cookieName.equals(cookie.getComment())){
                return Optional.of(CookieSession.deserialize(signer, cookie.getValue()));
            }
        }
        return CookieSession.noneOrEmptyOne(create);
    }

    @Override
    public void flush(HttpServletResponse response, CookieSession session) {
        baker.addCookie(response, signer, session);
    }
}