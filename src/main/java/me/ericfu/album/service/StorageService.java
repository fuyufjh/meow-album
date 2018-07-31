package me.ericfu.album.service;

import org.springframework.core.io.Resource;

import java.io.InputStream;

public interface StorageService {

    /**
     * Store a InputStream into persistent storage. Caller is responsible for closing the stream.
     */
    void store(InputStream file, String filename);

    Resource loadAsResource(String filename);

}
