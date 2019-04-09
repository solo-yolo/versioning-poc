package io.github.solo.yolo.dto;

import lombok.Data;

@Data
public class Acl {

    private String address;
    private Integer port;
    private String rule;
}
