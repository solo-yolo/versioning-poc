package io.github.solo.yolo.dto;

import lombok.Data;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

@Data
@Node
public class Neighbor {

    @Field
    private String name;
    @Field
    private Integer remoteAs;
    @Field
    private String remoteAddress;

}
