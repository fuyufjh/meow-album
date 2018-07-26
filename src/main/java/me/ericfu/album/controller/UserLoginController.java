package me.ericfu.album.controller;

import me.ericfu.album.exception.AuthFailedException;
import me.ericfu.album.model.User;
import me.ericfu.album.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class UserLoginController {

    private static final Logger logger = LoggerFactory.getLogger(UserLoginController.class);

    private final UserService userService;

    @Autowired
    public UserLoginController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginPage(HttpSession session) {
        if (session.getAttribute("user") != null) {
            return "redirect:/";
        }
        return "login";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpSession session, HttpServletResponse response) {
        session.removeAttribute("user");
        Cookie cookie = new Cookie("credential", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String handleLogin(@RequestParam("username") String username, @RequestParam("password") String password,
                              HttpServletResponse response, HttpSession session) {
        User user = userService.checkUsernamePassword(username, password);
        if (user == null) {
            throw new AuthFailedException("invalid username or password");
        }

        String credential = userService.buildCredential(user);
        Cookie cookie = new Cookie("credential", credential);
        response.addCookie(cookie);

        session.setAttribute("user", user);

        logger.info("User {} (id = {}) signed in successfully", user.getUsername(), user.getId());
        return "redirect:/";
    }

}
