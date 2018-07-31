package me.ericfu.album.service.impl;

import me.ericfu.album.dao.AlbumDao;
import me.ericfu.album.dao.PhotoDao;
import me.ericfu.album.model.Album;
import me.ericfu.album.model.Photo;
import me.ericfu.album.model.User;
import me.ericfu.album.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlbumServiceImpl implements AlbumService {
    private final AlbumDao albumDao;
    private final PhotoDao photoDao;

    @Autowired
    public AlbumServiceImpl(AlbumDao albumDao, PhotoDao photoDao) {
        this.albumDao = albumDao;
        this.photoDao = photoDao;
    }

    @Override
    public List<Album> getUserAlbums(User user) {
        return albumDao.findAlbumsByOwnerId(user.getId());
    }

    @Override
    public Album getAlbumByAlias(String alias) {
        return albumDao.getAlbumByAlias(alias);
    }

    @Override
    public List<Photo> getAlbumPhotos(Album album) {
        return photoDao.findPhotosByAlbumId(album.getId());
    }

    @Override
    public Photo addPhoto(Photo photo) {
        int photoId = photoDao.addPhoto(photo);
        photo.setId(photoId);
        return photo;
    }

    @Override
    public List<Album> getAllPublicAlbums() {
        return albumDao.getAllPublicAlbums();
    }
}
