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
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Aspect
@Order(0)
@Component
public class CheckSignedAspect {
    private static Logger logger = LoggerFactory.getLogger(CheckSignedAspect.class);

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Pointcut("execution(public * me.ericfu.album.controller..*(..))")
    public void atExecution() {
    }

    @Before("atExecution()")
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
            if (credential != null) {
                user = userService.checkCredential(credential);
                session.setAttribute("user", user);
                logger.info("User {} (id = {}) signed in with credential cookie", user.getUsername(), user.getId());
            }
        }
        request.setAttribute("user", user);
    }
}
