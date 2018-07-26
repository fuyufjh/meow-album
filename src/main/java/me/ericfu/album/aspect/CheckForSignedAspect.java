package me.ericfu.album.aspect;

import me.ericfu.album.exception.AuthFailedException;
import me.ericfu.album.model.User;
import me.ericfu.album.service.UserService;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Aspect
@Component
public class CheckForSignedAspect {
    private static Logger logger = LoggerFactory.getLogger(CheckForSignedAspect.class);

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Pointcut("@annotation(javax.annotation.CheckForSigned)")
    public void isAnnotated() {
    }

    @Pointcut("execution(public * me.ericfu.album..*(..))")
    public void atExecution() {
    }

    @Before("isAnnotated() && atExecution()")
    public void doBefore() throws AuthFailedException {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            String credential = null;
            for (Cookie cookie : request.getCookies()) {
                if ("credential".equals(cookie.getName())) {
                    credential = cookie.getValue();
                    break;
                }
            }
            if (credential == null) {
                throw new AuthFailedException("not signed in");
            } else {
                user = userService.checkCredential(credential);
                session.setAttribute("user", user);

                logger.info("User {} (id = {}) signed in with credential cookie", user.getUsername(), user.getId());
            }
        }
        request.setAttribute("user", user);
    }
}
