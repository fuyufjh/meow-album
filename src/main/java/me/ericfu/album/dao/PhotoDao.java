package me.ericfu.album.dao;

import me.ericfu.album.model.Photo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PhotoDao {

    @Select("SELECT * FROM photo WHERE id = #{photoId}")
    Photo getPhotoById(int photoId);

    @Select("SELECT * FROM photo WHERE album_id = #{albumId} ORDER BY id DESC")
    List<Photo> findPhotosByAlbumId(int albumId);

    @Insert("INSERT INTO photo (album_id, title, text, preview_url, raw_url, photo_time, public) VALUES (#{albumId}, #{title}, #{text}, #{previewUrl}, #{rawUrl}, #{photoTime}, #{public})")
    void addPhoto(Photo photo);
}
