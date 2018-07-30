package me.ericfu.album.service;

import me.ericfu.album.model.User;

public interface UserService {

    User getUserByUsername(String username);

    User checkUsernamePassword(String username, String password);
}
