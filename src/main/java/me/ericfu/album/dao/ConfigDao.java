package me.ericfu.album.dao;

import org.apache.ibatis.annotations.*;

@Mapper
public interface ConfigDao {

    @Select("SELECT `value` FROM config WHERE `key` = #{key}")
    String get(@Param("key") String key);

    @Insert("REPLACE INTO config (`key`, `value`) VALUES (#{key}, #{value})")
    void put(@Param("key") String key, @Param("value") String value);

    @Delete("DELETE FROM config WHERE `key` = #{key}")
    void delete(@Param("key") String key);
}
