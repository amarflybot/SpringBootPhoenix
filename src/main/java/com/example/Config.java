package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;
import java.sql.Driver;

/**
 * Created by amarendra on 14/04/17.
 */
@Configuration
public class Config {

    @Bean
    DataSource dataSource() {
        Driver driver = new org.apache.phoenix.jdbc.PhoenixDriver();
        String url = "jdbc:phoenix:127.0.0.1:2181/hbase";
        DataSource dataSource = new SimpleDriverDataSource(driver,url);
        return dataSource;
    }

    @Bean
    JdbcTemplate jdbcTemplate(final DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate;
    }
}
