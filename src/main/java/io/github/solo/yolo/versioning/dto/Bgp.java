package io.github.solo.yolo.versioning.dto;

import lombok.Data;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

import java.util.Collection;

@Data
@Node
public class Bgp {
    @Field
    private Integer asn;
    @org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection
    private Collection<Neighbor> neighbors;
}
