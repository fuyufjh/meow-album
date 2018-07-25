package me.ericfu.album.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.ericfu.album.dao.ConfigDao;
import me.ericfu.album.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Service
public class ConfigServiceImpl implements ConfigService {
    private static final Exception NULL_VALUE = new Exception();

    private final ConfigDao configDao;
    private final Cache<String, String> cache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build();

    @Autowired
    public ConfigServiceImpl(ConfigDao configDao) {
        this.configDao = configDao;
    }

    @Override
    public String get(String key) {
        try {
            return cache.get(key, () -> {
                String value = configDao.get(key);
                if (value == null) {
                    throw NULL_VALUE;
                }
                return value;
            });
        } catch (ExecutionException ex) {
            if (ex.getCause() == NULL_VALUE) {
                return null;
            }
            throw (RuntimeException) ex.getCause(); // must be ok
        }
    }

    @Override
    public String getOrUpdate(String key, Supplier<String> supplier) {
        String value = get(key);
        if (value == null) {
            value = supplier.get();
            put(key, value);
        }
        return value;
    }

    @Override
    public void put(String key, String value) {
        configDao.put(key, value);
        cache.put(key, value);
    }

    @Override
    public void delete(String key) {
        configDao.delete(key);
        cache.invalidate(key);
    }

}
