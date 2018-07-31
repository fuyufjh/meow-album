package me.ericfu.album.aspect;

import me.ericfu.album.exception.AuthFailedException;
import me.ericfu.album.model.User;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Set user to request attribute if presented in current session
 */
@Aspect
@Order(100)
@Component
public class SetUserInfoAspect {

    @Pointcut("execution(public * me.ericfu.album.controller..*(..))")
    public void isController() {
    }

    @Before("isController()")
    public void doBefore() throws AuthFailedException {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user != null) {
            request.setAttribute("user", user); // for future use in template
        }
    }

}
