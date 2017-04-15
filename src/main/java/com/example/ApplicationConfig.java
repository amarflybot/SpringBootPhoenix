package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.support.rowset.ResultSetWrappingSqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.sql.Driver;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by amarendra on 14/04/17.
 */
@Configuration
public class ApplicationConfig {

    @Autowired
    private DataSourceProperties dataSourceProperties;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Bean
    DataSource dataSource() {
        Driver driver = new org.apache.phoenix.jdbc.PhoenixDriver();
        String url = dataSourceProperties.getUrl();
        DataSource dataSource = new SimpleDriverDataSource(driver,url);
        return dataSource;
    }

    @Bean
    JdbcTemplate jdbcTemplate(final DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.setFetchSize(applicationProperties.getDatasource().getFetchSize());
        return jdbcTemplate;
    }

    @Bean
    public QueryStream streamer(JdbcTemplate jdbcTemplate) {
        return new QueryStream() {
            @Override
            public <T> T streamQuery(String sql, Function<Stream<SqlRowSet>, ? extends T> streamer, Object... args) {
                return jdbcTemplate.query(sql, resultSet -> {
                    final SqlRowSet rowSet = new ResultSetWrappingSqlRowSet(resultSet);

                    if (!rowSet.next()) {
                        return streamer.apply(StreamSupport.stream(Spliterators.emptySpliterator(),
                                IsParallel.PARALLEL.getFlag()));
                    }

                    Spliterator<SqlRowSet> spliterator = Spliterators.spliteratorUnknownSize(new Iterator<SqlRowSet>() {
                        private boolean first = true;
                        @Override
                        public boolean hasNext() {
                            return !rowSet.isLast();
                        }

                        @Override
                        public SqlRowSet next() {
                            if (!first || !rowSet.next()) {
                                throw new NoSuchElementException();
                            }
                            first = false;
                            return rowSet;
                        }
                    }, Spliterator.IMMUTABLE);
                    return streamer.apply(StreamSupport.stream(spliterator,
                            IsParallel.SEQUENTIAL.getFlag()));
                }, args);
            }
        };
    }
}
