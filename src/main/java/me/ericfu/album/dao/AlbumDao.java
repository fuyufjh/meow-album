package me.ericfu.album.dao;

import me.ericfu.album.model.Album;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AlbumDao {

    @Select("SELECT * FROM album WHERE alias = #{alias}")
    Album getAlbumByAlias(String alias);

    @Select("SELECT * FROM album WHERE owner_id = #{ownerId} ORDER BY id DESC")
    List<Album> findAlbumsByOwnerId(int ownerId);

    @Select("SELECT * FROM album WHERE public = TRUE")
    List<Album> getAllPublicAlbums();
}
