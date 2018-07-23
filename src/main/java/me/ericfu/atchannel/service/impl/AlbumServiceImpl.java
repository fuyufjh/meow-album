package me.ericfu.atchannel.service.impl;

import me.ericfu.atchannel.dao.AlbumDao;
import me.ericfu.atchannel.dao.PhotoDao;
import me.ericfu.atchannel.model.Album;
import me.ericfu.atchannel.model.Photo;
import me.ericfu.atchannel.model.User;
import me.ericfu.atchannel.service.AlbumService;
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
    public void addPhoto(Photo photo) {
        photoDao.addPhoto(photo);
    }
}
