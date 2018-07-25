package me.ericfu.album.controller;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import me.ericfu.album.Config;
import me.ericfu.album.exception.AuthFailedException;
import me.ericfu.album.model.User;
import me.ericfu.album.service.ConfigService;
import me.ericfu.album.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;
import java.security.SecureRandom;

@Controller
public class UserLoginController {

    private static final Logger logger = LoggerFactory.getLogger(UserLoginController.class);

    private static final HashFunction SHA256 = Hashing.sha256();
    private static final Charset CHARSET = Charset.defaultCharset();
    private static final String SEP = "|";

    private final UserService userService;
    private final ConfigService configService;

    @Autowired
    public UserLoginController(UserService userService, ConfigService configService) {
        this.userService = userService;
        this.configService = configService;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginPage(HttpServletResponse response) {
//        if (session.getAttribute("user") != null) {
//            return "redirect:/";
//        }
        return "login";
    }

    @RequestMapping(value = "/check", method = RequestMethod.GET)
    @ResponseBody
    public String handleCheck(@CookieValue("credential") String credential) {
        String[] splits = credential.split("\\|");
        String hmac = splits[0];
        String username = new String(Base64Utils.decodeFromString(splits[1]));
        long timestamp = Long.parseLong(splits[2]);

        User user = userService.getUserByUsername(username);
        if (user == null) {
            return "no such user";
        }

        String expected = hmac(username, user.getPassword(), timestamp);
        if (hmac.equals(expected)) {
            return "verify ok";
        } else {
            return "verify failed";
        }
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String handleLogin(@RequestParam("username") String username, @RequestParam("password") String password, HttpServletResponse response) {
        String hashedPassword = SHA256.hashString(password, CHARSET).toString();

        User user = userService.getUserByUsername(username);
        if (user == null || !user.getPassword().equals(hashedPassword)) {
            throw new AuthFailedException("invalid username or password");
        }

        long timestamp = System.currentTimeMillis();
        String credential = hmac(username, hashedPassword, timestamp) + SEP + Base64Utils.encodeToString(username.getBytes()) + SEP + Long.toString(timestamp);
        Cookie cookie = new Cookie("credential", credential);
        response.addCookie(cookie);

        logger.info("User {} (id = {}) login successfully", user.getUsername(), user.getId());
        return "redirect:/";
    }

    private String hmac(String username, String hashedPassword, long timestamp) {
        String data = username + SEP + hashedPassword + SEP + timestamp;
        return hmac(data);
    }

    private String hmac(String data) {
        String secretBase64 = configService.getOrUpdate(Config.COOKIE_SECRET, () -> {
            byte[] secret = new byte[32]; // 256 bits
            new SecureRandom().nextBytes(secret);
            return Base64Utils.encodeToString(secret);
        });
        byte[] secret = Base64Utils.decodeFromString(secretBase64);
        HashFunction hmac = Hashing.hmacSha256(secret);
        return hmac.hashString(data, CHARSET).toString();
    }
}
