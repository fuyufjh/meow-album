package me.ericfu.album.controller;

import me.ericfu.album.aspect.CheckSignedAspect.CheckSigned;
import me.ericfu.album.exception.AuthFailedException;
import me.ericfu.album.exception.PhotoException;
import me.ericfu.album.exception.ResourceNotFoundException;
import me.ericfu.album.model.Album;
import me.ericfu.album.model.Photo;
import me.ericfu.album.model.User;
import me.ericfu.album.service.AlbumService;
import me.ericfu.album.service.StorageService;
import me.ericfu.album.util.PhotoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Controller
public class AlbumController {

    private final AlbumService albumService;
    private final StorageService storageService;

    @Autowired
    public AlbumController(AlbumService albumService, StorageService storageService) {
        this.albumService = albumService;
        this.storageService = storageService;
    }

    @GetMapping("/")
    public ModelAndView homepage(HttpSession session) {
        List<Album> albums = albumService.getAllPublicAlbums();

        ModelAndView view = new ModelAndView("home");
        view.addObject("albums", albums);
        view.addObject("user", session.getAttribute("user"));
        return view;
    }

    @GetMapping("/my")
    @CheckSigned
    public ModelAndView myAlbums(HttpSession session) {
        User user = (User) session.getAttribute("user");
        List<Album> albums = albumService.getUserAlbums(user);

        ModelAndView view = new ModelAndView("my_albums");
        view.addObject("albums", albums);
        view.addObject("user", session.getAttribute("user"));
        return view;
    }

    @GetMapping("/album/{alias}")
    public ModelAndView showAlbumByAlias(@PathVariable("alias") String alias, HttpSession session) {
        Album album = albumService.getAlbumByAlias(alias);
        if (album == null) {
            throw new ResourceNotFoundException("requested album not found");
        }
        if (!album.isPublic()) {
            User user = (User) session.getAttribute("user");
            if (user == null || album.getOwnerId() != user.getId()) {
                throw new AuthFailedException("no permission to this album");
            }
        }
        List<Photo> photos = albumService.getAlbumPhotos(album);

        ModelAndView view = new ModelAndView("album");
        view.addObject("album", album);
        view.addObject("photos", photos);
        view.addObject("user", session.getAttribute("user"));
        return view;
    }

    @PostMapping("/album/{alias}")
    @CheckSigned
    public String uploadPhoto(@PathVariable("alias") String alias,
                              @RequestParam("title") String title,
                              @RequestParam("text") String text,
                              @RequestParam("files") MultipartFile[] files,
                              HttpSession session) throws IOException {
        User user = (User) session.getAttribute("user");
        Album album = albumService.getAlbumByAlias(alias);
        if (album == null) {
            throw new ResourceNotFoundException("requested album not found");
        }
        if (album.getOwnerId() != user.getId()) {
            throw new AuthFailedException("no permission to this album");
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw new PhotoException("empty file");
            }

            String extName = PhotoUtils.getFileExtName(file);
            String hashCode = PhotoUtils.getHashCode(file);
            String rawFilename = hashCode + extName;
            String thumbnailFilename = hashCode + ".thumbnail" + extName;

            try (InputStream rotatedStream = PhotoUtils.getRotatedLossless(file, rawFilename)) {
                storageService.store(rotatedStream, rawFilename);
            }

            try (InputStream rawStream = file.getInputStream();
                 InputStream inStream = PhotoUtils.getThumbnail(rawStream)) {
                storageService.store(inStream, thumbnailFilename);
            }

            Photo photo = new Photo();
            photo.setAlbumId(album.getId());
            photo.setTitle(title);
            photo.setText(text);
            photo.setPhotoTime(PhotoUtils.extractDate(file));

            String rawPhotoUrl = "/upload/" + rawFilename;
            photo.setRawUrl(rawPhotoUrl);
            String thumbnailPhotoUrl = "/upload/" + thumbnailFilename;
            photo.setPreviewUrl(thumbnailPhotoUrl);
            albumService.addPhoto(photo);
        }

        return "redirect:/album/" + alias;
    }

    @GetMapping("/upload/{filename}")
    @ResponseBody
    public ResponseEntity<Resource> getPhoto(@PathVariable("filename") String filename) {
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE)
                .body(file);
    }
}
