package me.ericfu.album.service;

import me.ericfu.album.model.User;

public interface UserService {

    User checkUserCredential(String username, String password);

}
