package io.github.solo.yolo.versioning.service;

import io.github.solo.yolo.versioning.dto.RouterConfiguration;
import io.github.solo.yolo.versioning.dto.VersionInfo;
import io.github.solo.yolo.versioning.dto.VersionWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.jackrabbit.ocm.manager.ObjectContentManager;
import org.apache.jackrabbit.ocm.version.Version;
import org.apache.jackrabbit.ocm.version.VersionIterator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import javax.jcr.RepositoryException;
import javax.jcr.version.VersionException;
import javax.jcr.version.VersionManager;

@Slf4j
@Component
@RequiredArgsConstructor
public class JackrabbitVersioningService implements VersioningService {

    private static final String ROOT_VERSION_NAME = "jcr:rootVersion";
    private final ObjectContentManager ocm;

    @Override
    public void checkpoint(String id, RouterConfiguration item, String... labels) {
        final String path = toPath(id);

        try {

            if (!ocm.objectExists(path)) {
                create(item, path, labels);
            } else if (item != null) {
                update(item, path, labels);
            } else {
                delete(path, labels);
            }

        } catch (VersionException e) {
            throw new VersioningException("failed to create node version", e);
        }
    }

    private void create(RouterConfiguration item, String path, String[] labels) throws VersionException {
        item.setPath(path);
        ocm.insert(item);
        ocm.save();
        ocm.checkin(path, labels);
        ocm.checkout(path);
    }

    private void update(RouterConfiguration item, String path, String[] labels) throws VersionException {
        item.setPath(path);
        ocm.checkout(path);
        ocm.update(item);
        ocm.save();
        ocm.checkin(path, labels);
    }

    private void delete(String path, String[] labels) throws VersionException {
        ocm.checkout(path);
        ocm.remove(path);
        ocm.save();
        ocm.checkin(path, labels);
    }

    @Override
    public VersionWrapper<RouterConfiguration> get(String id, String version) {
        final String path = toPath(id);

        try {
            Version v = ocm.getVersion(path, version);
            String[] labels = ocm.getVersionLabels(path, version);

            return VersionWrapper.<RouterConfiguration>builder()
                    .item((RouterConfiguration) ocm.getObject(path, version))
                    .labels(Arrays.asList(labels))
                    .time(v.getCreated().toInstant().toString())
                    .version(v.getName())
                    .build();

        } catch (VersionException e) {
            throw new VersioningException(String.format("failed to get node (%s) version (%s)", path, version), e);
        }
    }

    @Override
    public Collection<VersionWrapper<RouterConfiguration>> get(String id) {
        return getVersions(id).stream()
                .map(version -> get(id, version.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public VersionInfo getVersionInfo(String id, String version) {
        final String path = toPath(id);
        try {
            Version v = ocm.getVersion(path, version);
            return VersionInfo.builder()
                    .labels(Arrays.asList(ocm.getVersionLabels(path, version)))
                    .name(v.getName())
                    .time(v.getCreated().toInstant().toString())
                    .build();

        } catch (VersionException e) {
            throw new VersioningException(String.format("failed to get node (%s) version (%s)", path, version), e);
        }
    }

    @Override
    public Collection<VersionInfo> getVersionsInfo(String id) {
        return getVersions(id).stream()
                .map(v -> getVersionInfo(id, v.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public void removeVersion(String id, String version) {
        String path = toPath(id);

        try {
            VersionManager versionManager = ocm.getSession().getWorkspace().getVersionManager();
            versionManager.getVersionHistory(path).removeVersion(version);

        } catch (RepositoryException e) {
            throw new VersioningException(String.format("failed to remove node (%s) version (%s)", path, version), e);
        }
    }

    private Collection<Version> getVersions(String id) {
        final String path = toPath(id);
        try {
            Collection<Version> result = new ArrayList<>();
            VersionIterator iterator = ocm.getAllVersions(path);
            while (iterator.hasNext()) {
                Version next = (Version) iterator.next();
                if (!ROOT_VERSION_NAME.equals(next.getName())) {
                    result.add(next);
                }
            }
            return result;
        } catch (VersionException e) {
            throw new VersioningException(String.format("failed to get node (%s) versions", path), e);
        }
    }

    private static String toPath(String id) {
        return id.startsWith("/") ? id : "/" + id;
    }

}
