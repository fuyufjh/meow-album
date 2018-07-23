package me.ericfu.atchannel.service;

import me.ericfu.atchannel.model.Album;
import me.ericfu.atchannel.model.Photo;
import me.ericfu.atchannel.model.User;

import java.util.List;

public interface AlbumService {

    List<Album> getUserAlbums(User user);

    Album getAlbumByAlias(String alias);

    List<Photo> getAlbumPhotos(Album album);

    void addPhoto(Photo photo);
}
