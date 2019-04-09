package io.github.solo.yolo.resource;


import io.github.solo.yolo.dto.RouterConfiguration;
import io.github.solo.yolo.dto.VersionInfo;
import io.github.solo.yolo.dto.VersionWrapper;
import io.github.solo.yolo.service.NorthboundService;
import io.github.solo.yolo.service.VersioningService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/router")
@RequiredArgsConstructor
public class ConfigController {

    private final AtomicInteger counter = new AtomicInteger();
    private final NorthboundService northbound;
    private final VersioningService versioning;

    @GetMapping
    public Collection<RouterConfiguration> list() {
        return northbound.all();
    }

    @GetMapping("/{id}")
    public RouterConfiguration get(@PathVariable String id) {
        return northbound.get(id);
    }

    @PostMapping
    public RouterConfiguration create(@RequestBody RouterConfiguration payload) {
        RouterConfiguration configuration = northbound.create(payload);
        versioning.checkpoint(configuration.getId(), configuration, getGlobalCounter());
        return configuration;
    }

    @PutMapping("/{id}")
    public RouterConfiguration update(@PathVariable String id, @RequestBody RouterConfiguration payload) {
        RouterConfiguration configuration = northbound.update(id, payload);
        versioning.checkpoint(configuration.getId(), configuration, getGlobalCounter());
        return configuration;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        northbound.remove(id);
        versioning.checkpoint(id, null,  getGlobalCounter());
    }

    @GetMapping("/{id}/versions")
    public Collection<VersionWrapper<RouterConfiguration>> getVersions(@PathVariable String id) {
        return versioning.get(id);
    }

    @GetMapping("/{id}/versions/{version}")
    public VersionWrapper<RouterConfiguration> getVersion(@PathVariable String id, @PathVariable String version) {
        return versioning.get(id, version);
    }

    @GetMapping("/{id}/versions/info")
    public Collection<VersionInfo> getVersionsInfo(@PathVariable String id) {
        return versioning.getVersionsInfo(id);
    }

    @DeleteMapping("/{id}/versions/{version}")
    public void deleteVersion(@PathVariable String id, @PathVariable String version) {
        versioning.removeVersion(id, version);
    }

    private String getGlobalCounter() {
        return String.valueOf(counter.incrementAndGet());
    }

}
