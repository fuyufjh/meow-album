package me.ericfu.album.service;

import me.ericfu.album.model.Album;
import me.ericfu.album.model.Photo;
import me.ericfu.album.model.User;

import java.util.List;

public interface AlbumService {

    List<Album> getUserAlbums(User user);

    Album getAlbumByAlias(String alias);

    List<Photo> getAlbumPhotos(Album album);

    void addPhoto(Photo photo);

    List<Album> getAllPublicAlbums();
}
