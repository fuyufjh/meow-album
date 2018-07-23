package me.ericfu.album.service.impl;

import me.ericfu.album.dao.UserDao;
import me.ericfu.album.model.User;
import me.ericfu.album.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    @Autowired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User checkUserCredential(String username, String password) {
        return userDao.getUserLogin(username, md5Hash(password));
    }

    private static String md5Hash(String plaintext) {
        // Ref. https://stackoverflow.com/a/30119004/5739882
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(StandardCharsets.UTF_8.encode(plaintext));
            return String.format("%032x", new BigInteger(1, md.digest()));
        } catch (NoSuchAlgorithmException e) {
            // Impossible
            throw new RuntimeException("unreachable");
        }
    }
}
