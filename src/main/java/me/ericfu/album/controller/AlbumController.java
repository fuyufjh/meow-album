package me.ericfu.album.controller;

import me.ericfu.album.aspect.MustSigned;
import me.ericfu.album.exception.AuthFailedException;
import me.ericfu.album.exception.ResourceNotFoundException;
import me.ericfu.album.model.Album;
import me.ericfu.album.model.Photo;
import me.ericfu.album.model.User;
import me.ericfu.album.service.AlbumService;
import me.ericfu.album.service.StorageService;
import me.ericfu.album.util.PhotoUtils;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

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
    public String homepage(ModelMap modelMap) {
        List<Album> albums = albumService.getAllPublicAlbums();
        modelMap.addAttribute("albums", albums);
        return "home";
    }

    @GetMapping("/my")
    @MustSigned
    public String myAlbums(ModelMap modelMap, HttpSession session) {
        User user = (User) session.getAttribute("user");
        List<Album> albums = albumService.getUserAlbums(user);
        modelMap.addAttribute("albums", albums);
        return "my_albums";
    }

    @GetMapping("/album/{alias}")
    public String showAlbumByAlias(@PathVariable("alias") String alias, HttpSession session, ModelMap modelMap) {
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
        modelMap.addAttribute("album", album);
        modelMap.addAttribute("photos", photos);
        return "album";
    }

    @PostMapping("/album/{alias}")
    @MustSigned
    public String uploadPhoto(@PathVariable("alias") String alias,
                              @RequestParam("title") String title,
                              @RequestParam("text") String text,
                              @RequestParam("file") MultipartFile file,
                              HttpSession session) {
        User user = (User) session.getAttribute("user");
        Album album = albumService.getAlbumByAlias(alias);
        if (album == null) {
            throw new ResourceNotFoundException("requested album not found");
        }
        if (album.getOwnerId() != user.getId()) {
            throw new AuthFailedException("no permission to this album");
        }

        String filename = UUID.randomUUID().toString() + ".jpg";
        storageService.store(file, filename);

        Photo photo = new Photo();
        photo.setAlbumId(album.getId());
        photo.setTitle(title);
        photo.setText(text);
        photo.setPhotoTime(PhotoUtils.extractDate(file));

        String photoUrl = "/files/" + filename;
        photo.setRawUrl(photoUrl);
        photo.setPreviewUrl(photoUrl);
        albumService.addPhoto(photo);

        return "redirect:/album/" + alias;
    }

    @GetMapping("/files/{filename}")
    @ResponseBody
    public ResponseEntity<Resource> getPhoto(@PathVariable("filename") String filename,
                           HttpSession session,
                           ModelMap modelMap) throws IOException {
        // FIXME: thumbnail should be generated when uploading, just hacking
        Resource file = storageService.loadAsResource(filename);
        ByteArrayOutputStream thumbnailStream = new ByteArrayOutputStream();
        Thumbnails.of(file.getURL()).size(1024, 1024).toOutputStream(thumbnailStream);
        ByteArrayResource resource = new ByteArrayResource(thumbnailStream.toByteArray());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE)
                .body(resource);
    }
}
