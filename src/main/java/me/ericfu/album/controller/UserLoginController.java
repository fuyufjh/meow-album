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

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String handleLogin(@RequestParam("username") String username, @RequestParam("password") String password, HttpSession session) {
        User user = userService.checkUserCredential(username, password);
        if (user == null) {
            throw new AuthFailedException("invalid username or password");
        }
        session.setAttribute("user", user);
        logger.info("User {} (id = {}) login successfully", user.getUsername(), user.getId());
        return "redirect:/";
    }
}
