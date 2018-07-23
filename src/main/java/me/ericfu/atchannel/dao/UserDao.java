package me.ericfu.atchannel.dao;

import me.ericfu.atchannel.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserDao {

    @Select("SELECT * FROM user WHERE username = #{username} AND password = #{password}")
    User getUserLogin(String username, String password);

    @Select("SELECT * FROM user WHERE id = ${userId}")
    User getUserById(int userId);
}
