package me.ericfu.album.service.impl;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import me.ericfu.album.dao.UserDao;
import me.ericfu.album.model.User;
import me.ericfu.album.service.ConfigService;
import me.ericfu.album.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;

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
    public User checkUsernamePassword(String username, String password) {
        String hashedPassword = SHA256.hashString(password, CHARSET).toString();
        return userDao.getUserLogin(username, hashedPassword);
    }
}
