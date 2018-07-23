package me.ericfu.atchannel.controller;

import me.ericfu.atchannel.exception.ResourceNotFoundException;
import me.ericfu.atchannel.model.Album;
import me.ericfu.atchannel.model.Photo;
import me.ericfu.atchannel.model.User;
import me.ericfu.atchannel.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AlbumController {

    private final AlbumService albumService;

    private final User mockUser;
    {
        mockUser = new User();
        mockUser.setId(1);
        mockUser.setUsername("fuyufjh");
        mockUser.setPassword("e10adc3949ba59abbe56e057f20f883e"); // plain text is "123456"
    }

    @Autowired
    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String homepage(ModelMap modelMap) {
        List<Album> albums = albumService.getUserAlbums(mockUser);
        modelMap.addAttribute("albums", albums);
        return "index";
    }

    @RequestMapping(value = "/album/{alias}", method = RequestMethod.GET)
    public String findCommentsByPostAddress(@PathVariable("alias") String alias, ModelMap modelMap) {
        Album album = albumService.getAlbumByAlias(alias);
        if (album == null) {
            throw new ResourceNotFoundException("requested album not found");
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
