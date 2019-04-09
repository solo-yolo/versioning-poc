package io.github.solo.yolo.dto;

import lombok.Data;

import java.util.Collection;

@Data
public class Bgp {

    private Integer asn;
    private Collection<Neighbor> neighbors;
}
