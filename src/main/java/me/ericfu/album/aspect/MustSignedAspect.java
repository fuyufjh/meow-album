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

@Aspect
@Order(10)
@Component
public class MustSignedAspect {

    @Pointcut("@annotation(me.ericfu.album.aspect.MustSigned)")
    public void isAnnotated() {
    }

    @Pointcut("execution(public * me.ericfu.album.controller..*(..))")
    public void atExecution() {
    }

    @Before("isAnnotated() && atExecution()")
    public void doBefore() throws AuthFailedException {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new AuthFailedException("not signed in");
        }
    }
}
