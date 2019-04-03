package io.github.solo.yolo.service;

import io.github.solo.yolo.dto.RouterConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class NorthboundService {

    private final ConcurrentHashMap<String, RouterConfiguration> datastore = new ConcurrentHashMap<>();

    public RouterConfiguration get(String id) {
        return Optional.ofNullable(datastore.get(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public RouterConfiguration create(RouterConfiguration configuration) {
        String id = UUID.randomUUID().toString();
        configuration.setId(id);
        datastore.put(id, configuration);
        return configuration;
    }

    public RouterConfiguration update(String id, RouterConfiguration configuration) {
        if (datastore.containsKey(id)) {
            configuration.setId(id);
            datastore.put(id, configuration);
            return configuration;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public void remove(String id) {
        if (datastore.containsKey(id)) {
            datastore.remove(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public Collection<RouterConfiguration> all() {
        return datastore.values();
    }
}
