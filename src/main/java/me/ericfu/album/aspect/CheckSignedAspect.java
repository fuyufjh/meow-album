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
import java.lang.annotation.*;

@Aspect
@Order(10)
@Component
public class CheckSignedAspect {

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    public @interface CheckSigned {
    }

    @Pointcut("@annotation(CheckSignedAspect.CheckSigned)")
    public void isAnnotated() {
    }

    @Pointcut("execution(public * me.ericfu.album.controller..*(..))")
    public void isController() {
    }

    @Before("isAnnotated() && isController()")
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
