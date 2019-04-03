package io.github.solo.yolo.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Collection;

@Data
@Builder
public class VersionWrapper<T> {
    private T item;
    private String version;
    private String time;
    private Collection<String> labels;
}
