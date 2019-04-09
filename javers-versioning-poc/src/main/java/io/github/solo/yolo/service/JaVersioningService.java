package io.github.solo.yolo.service;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import io.github.solo.yolo.dto.RouterConfiguration;
import io.github.solo.yolo.dto.VersionInfo;
import io.github.solo.yolo.dto.VersionWrapper;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.javers.core.Javers;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.jql.JqlQuery;
import org.javers.repository.jql.QueryBuilder;
import org.javers.shadow.Shadow;
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
    private final MongoClient mongoClient;

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
        return get(id).stream()
                .filter(v -> v.getVersion().equals(version))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public Collection<VersionWrapper<RouterConfiguration>> get(String id) {
        JqlQuery query = QueryBuilder.byInstanceId(id, RouterConfiguration.class).build();

        List<CdoSnapshot> snapshots = javers.findSnapshots(query);
        List<Shadow<RouterConfiguration>> shadows = javers.findShadows(query);

        return snapshots.stream()
                .map(s -> convertToWrapper(s, shadows.stream()
                        .filter(shadow -> shadow.getCommitId().equals(s.getCommitId()))
                        .findFirst()))
                .collect(Collectors.toList());
    }

    VersionWrapper<RouterConfiguration> convertToWrapper(CdoSnapshot snapshot,
            Optional<Shadow<RouterConfiguration>> shadow) {
        return VersionWrapper.<RouterConfiguration>builder()
                .item(shadow.map(Shadow::get).orElse(null))
                .version(String.valueOf(snapshot.getVersion()))
                .time(snapshot.getCommitMetadata().getCommitDateInstant().toString())
                .labels(snapshot.getCommitMetadata().getProperties().values())
                .build();
    }

    @Override
    public VersionInfo getVersionInfo(String id, String version) {
        JqlQuery query = QueryBuilder.byInstanceId(id, RouterConfiguration.class)
                .withVersion(Long.valueOf(version)).build();

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
        MongoCollection<Document> collection = mongoClient.getDatabase("javers").getCollection("jv_snapshots");

        Document document = collection.findOneAndDelete(and(
                eq("globalId_key", "router-config/" + id),
                eq("version", Long.valueOf(version))));

        if (document == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

    }
}
