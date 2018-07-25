package me.ericfu.album.dao;

import me.ericfu.album.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserDao {

    @Select("SELECT * FROM user WHERE username = #{username} AND password = #{password}")
    User getUserLogin(@Param("username") String username, @Param("password") String password);

    @Select("SELECT * FROM user WHERE id = ${userId}")
    User getUserById(@Param("userId") int userId);

    @Select("SELECT * FROM user WHERE username = #{username}")
    User getUserByName(@Param("username") String username);
}
