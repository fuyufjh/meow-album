package me.ericfu.album.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    void store(MultipartFile file, String filename);

    Resource loadAsResource(String filename);

}
