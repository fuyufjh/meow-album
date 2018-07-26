package me.ericfu.album.service;

import me.ericfu.album.model.User;

public interface UserService {

    User getUserByUsername(String username);

    User checkCredential(String credential);

    User checkUsernamePassword(String username, String password);

    String buildCredential(User user);
}
