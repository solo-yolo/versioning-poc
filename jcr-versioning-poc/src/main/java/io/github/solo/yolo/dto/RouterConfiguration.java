package io.github.solo.yolo.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Bean;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

import java.util.Collection;

@Data
@Node(jcrMixinTypes = "mix:versionable")
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RouterConfiguration {

    @JsonIgnore
    @Field(path = true)
    private String path;
    @Field
    private String id;
    @Field
    private String name;
    @Field
    private String description;
    @org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection
    private Collection<Interface> interfaces;
    @org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection
    private Collection<Acl> acl;
    @Bean
    private Bgp bgp;
}


