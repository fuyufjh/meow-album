package me.ericfu.atchannel.dao;

import me.ericfu.atchannel.model.Album;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AlbumDao {

    @Select("SELECT * FROM album WHERE alias = #{alias}")
    Album getAlbumByAlias(String alias);

    @Select("SELECT * FROM album WHERE owner_id = #{ownerId} ORDER BY id DESC")
    List<Album> findAlbumsByOwnerId(int ownerId);
}
