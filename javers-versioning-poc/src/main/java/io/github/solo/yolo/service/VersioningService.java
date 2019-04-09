package io.github.solo.yolo.service;

import io.github.solo.yolo.dto.RouterConfiguration;
import io.github.solo.yolo.dto.VersionInfo;
import io.github.solo.yolo.dto.VersionWrapper;

import java.util.Collection;

public interface VersioningService {

    void checkpoint(String id, RouterConfiguration item, String... labels);

    VersionWrapper<RouterConfiguration> get(String id, String version);

    Collection<VersionWrapper<RouterConfiguration>> get(String id);

    VersionInfo getVersionInfo(String id, String version);

    Collection<VersionInfo> getVersionsInfo(String id);

    void removeVersion(String id, String version);

}
