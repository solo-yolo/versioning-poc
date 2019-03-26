package io.github.solo.yolo.versioning;

import io.github.solo.yolo.versioning.dto.Acl;
import io.github.solo.yolo.versioning.dto.Bgp;
import io.github.solo.yolo.versioning.dto.Interface;
import io.github.solo.yolo.versioning.dto.Neighbor;
import io.github.solo.yolo.versioning.dto.RouterConfiguration;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.ocm.manager.ObjectContentManager;
import org.apache.jackrabbit.ocm.manager.impl.ObjectContentManagerImpl;
import org.apache.jackrabbit.ocm.mapper.Mapper;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.AnnotationMapperImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

@Configuration
public class AppConfig {

    @Bean(destroyMethod = "logout")
    Session session() throws RepositoryException {
        return JcrUtils.getRepository().login(new SimpleCredentials("admin", "admin".toCharArray()));
    }

    @Bean
    ObjectContentManager manager(Session session) {
        Mapper mapper = new AnnotationMapperImpl(Arrays.asList(
                RouterConfiguration.class, Bgp.class, Acl.class, Interface.class, Neighbor.class
        ));
        return new ObjectContentManagerImpl(session, mapper);
    }

}
