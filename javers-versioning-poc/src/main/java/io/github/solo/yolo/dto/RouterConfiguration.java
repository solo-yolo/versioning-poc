package io.github.solo.yolo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.TypeName;

import java.util.Collection;


@TypeName("router-config")
@Data
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RouterConfiguration {

    @Id
    private String id;
    private String name;
    private String description;
    private Collection<Interface> interfaces;
    private Collection<Acl> acl;
    private Bgp bgp;
}


