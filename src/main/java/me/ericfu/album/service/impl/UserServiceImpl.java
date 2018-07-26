package me.ericfu.album.service.impl;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import me.ericfu.album.Config;
import me.ericfu.album.dao.UserDao;
import me.ericfu.album.model.User;
import me.ericfu.album.service.ConfigService;
import me.ericfu.album.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import java.nio.charset.Charset;
import java.security.SecureRandom;

@Service
public class UserServiceImpl implements UserService {
    private static final HashFunction SHA256 = Hashing.sha256();
    private static final Charset CHARSET = Charset.defaultCharset();
    private static final String SEP = "|";

    private final UserDao userDao;
    private final ConfigService configService;

    @Autowired
    public UserServiceImpl(UserDao userDao, ConfigService configService) {
        this.userDao = userDao;
        this.configService = configService;
    }

    @Override
    public User getUserByUsername(String username) {
        return userDao.getUserByName(username);
    }

    @Override
    public User checkCredential(String credential) {
        String[] splits = credential.split("\\|");
        String hmac = splits[0];
        String username = new String(Base64Utils.decodeFromString(splits[1]));
        long timestamp = Long.parseLong(splits[2]);

        User user = getUserByUsername(username);
        if (user == null) {
            return null; // No such user
        }

        String expected = hmac(username, user.getPassword(), timestamp);
        if (hmac.equals(expected)) {
            return user;
        } else {
            return null; // HMAC verify failed
        }
    }

    @Override
    public User checkUsernamePassword(String username, String password) {
        String hashedPassword = SHA256.hashString(password, CHARSET).toString();
        return userDao.getUserLogin(username, hashedPassword);
    }

    @Override
    public String buildCredential(User user) {
        long timestamp = System.currentTimeMillis();
        return hmac(user.getUsername(), user.getPassword(), timestamp)
                + SEP + Base64Utils.encodeToString(user.getUsername().getBytes())
                + SEP + Long.toString(timestamp);
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
