package io.github.solo.yolo.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Collection;

@Data
@Builder
public class VersionInfo {
    private String name;
    private Collection<String> labels;
    private String time;
}
