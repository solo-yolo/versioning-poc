package io.github.solo.yolo.versioning.dto;

import lombok.Data;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

@Data
@Node
public class Acl {

    @Field
    private String address;
    @Field
    private Integer port;
    @Field
    private String rule;
}
