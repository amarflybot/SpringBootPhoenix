package com.example;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

/**
 * Created by amarendra on 14/04/17.
 */
@Component
public class JerseyConfig extends ResourceConfig{

    public JerseyConfig() {
        register(DataSourceResource.class);
    }


}
