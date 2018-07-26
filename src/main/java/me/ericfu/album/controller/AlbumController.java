package me.ericfu.album.controller;

import me.ericfu.album.exception.AuthFailedException;
import me.ericfu.album.exception.ResourceNotFoundException;
import me.ericfu.album.model.Album;
import me.ericfu.album.model.Photo;
import me.ericfu.album.model.User;
import me.ericfu.album.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.CheckForSigned;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class AlbumController {

    private final AlbumService albumService;

    @Autowired
    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @CheckForSigned
    public String homepage(ModelMap modelMap, HttpSession session) {
        User user = (User) session.getAttribute("user");
        List<Album> albums = albumService.getUserAlbums(user);
        modelMap.addAttribute("albums", albums);
        return "index";
    }

    @RequestMapping(value = "/album/{alias}", method = RequestMethod.GET)
    @CheckForSigned
    public String showAlbumByAlias(@PathVariable("alias") String alias, HttpSession session, ModelMap modelMap) {
        User user = (User) session.getAttribute("user");
        Album album = albumService.getAlbumByAlias(alias);
        if (album == null) {
            throw new ResourceNotFoundException("requested album not found");
        }
        if (album.getOwnerId() != user.getId()) {
            throw new AuthFailedException("no permission to this album");
        }
        List<Photo> photos = albumService.getAlbumPhotos(album);
        modelMap.addAttribute("album", album);
        modelMap.addAttribute("photos", photos);
        return "album";
    }

//    @RequestMapping(value = "/album/{alias}", method = RequestMethod.POST)
//    public String addComment(@PathVariable("alias") String alias,
//                             @RequestParam("author") String author,
//                             @RequestParam("content") String content) {
//        Channel channel = channelService.findChannelByAddress(address);
//        if (channel == null) {
//            throw new ResourceNotFoundException("channel not found");
//        }
//        Comment comment = new Comment();
//        comment.setChannelId(channel.getId());
//        comment.setAuthor(author);
//        comment.setContent(content);
//        channelService.addComment(comment);
//        return "redirect:" + address;
//    }
}
