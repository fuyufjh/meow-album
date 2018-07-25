package me.ericfu.album.service;

import java.util.function.Supplier;

public interface ConfigService {

    String get(String key);

    void put(String key, String value);

    void delete(String key);

    String getOrUpdate(String key, Supplier<String> supplier);
}
