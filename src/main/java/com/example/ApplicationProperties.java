package com.example;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by amarendra on 14/04/17.
 */
@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {

    private final Datasource datasource = new Datasource();

    public Datasource getDatasource() {
        return datasource;
    }

    public static class Datasource {

        private Integer fetchSize;

        public Integer getFetchSize() {
            return fetchSize;
        }

        public void setFetchSize(Integer fetchSize) {
            this.fetchSize = fetchSize;
        }

    }
}
