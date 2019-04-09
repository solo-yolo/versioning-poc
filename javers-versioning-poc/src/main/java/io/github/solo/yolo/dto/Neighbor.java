package io.github.solo.yolo.dto;

import lombok.Data;

@Data
public class Neighbor {

    private String name;
    private Integer remoteAs;
    private String remoteAddress;

}
