package io.github.solo.yolo.service;

import com.google.gson.JsonObject;
import io.github.solo.yolo.dto.RouterConfiguration;
import io.github.solo.yolo.dto.VersionInfo;
import io.github.solo.yolo.dto.VersionWrapper;
import lombok.RequiredArgsConstructor;
import org.javers.core.Javers;
import org.javers.core.commit.CommitId;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.CdoSnapshotState;
import org.javers.repository.jql.JqlQuery;
import org.javers.repository.jql.QueryBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JaVersioningService implements VersioningService {

    private final Javers javers;
    private final JsonConverter jsonConverter;

    @Override
    public void checkpoint(String id, RouterConfiguration item, String... labels) {
        Map<String, String> props = Optional.ofNullable(labels).map(s -> String.join(", ", s)).map(s -> {
            Map<String, String> result = new HashMap<>();
            result.put("labels", s);
            return result;
        }).orElse(Collections.emptyMap());

        javers.commit("customer with " + UUID.randomUUID().toString(), item, props);
    }

    @Override
    public VersionWrapper<RouterConfiguration> get(String id, String version) {
        JqlQuery query = QueryBuilder.byInstanceId(id, RouterConfiguration.class)
                .withCommitId(CommitId.valueOf(version)).build();

        List<CdoSnapshot> snapshots = javers.findSnapshots(query);

        return snapshots.stream()
                .findFirst()
                .map(this::convertToWrapper)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public Collection<VersionWrapper<RouterConfiguration>> get(String id) {
        JqlQuery query = QueryBuilder.byInstanceId(id, RouterConfiguration.class).build();

        List<CdoSnapshot> snapshots = javers.findSnapshots(query);

        return snapshots.stream().map(this::convertToWrapper).collect(Collectors.toList());
    }

    VersionWrapper<RouterConfiguration> convertToWrapper(CdoSnapshot snapshot) {
        return VersionWrapper.<RouterConfiguration>builder()
                .item(convert(snapshot.getState()))
                .version(String.valueOf(snapshot.getVersion()))
                .time(snapshot.getCommitMetadata().getCommitDateInstant().toString())
                .labels(snapshot.getCommitMetadata().getProperties().values())
                .build();
    }

    RouterConfiguration convert(CdoSnapshotState snapshotState) {
        JsonObject json = (JsonObject) jsonConverter.toJsonElement(snapshotState);
        return jsonConverter.fromJson(json.get("properties"), RouterConfiguration.class);
    }

    @Override
    public VersionInfo getVersionInfo(String id, String version) {
        JqlQuery query = QueryBuilder.byInstanceId(id, RouterConfiguration.class)
                .withCommitId(CommitId.valueOf(version)).build();

        List<CdoSnapshot> snapshots = javers.findSnapshots(query);

        return snapshots.stream()
                .findFirst()
                .map(this::convert)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public Collection<VersionInfo> getVersionsInfo(String id) {
        JqlQuery query = QueryBuilder.byInstanceId(id, RouterConfiguration.class).build();
        List<CdoSnapshot> snapshots = javers.findSnapshots(query);

        return snapshots.stream().map(this::convert).collect(Collectors.toList());
    }

    VersionInfo convert(CdoSnapshot snapshot) {
        return VersionInfo.builder()
                .name(String.valueOf(snapshot.getVersion()))
                .labels(snapshot.getCommitMetadata().getProperties().values())
                .time(snapshot.getCommitMetadata().getCommitDateInstant().toString())
                .build();
    }

    @Override
    public void removeVersion(String id, String version) {
        throw new UnsupportedOperationException();
    }
}
