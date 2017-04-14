package com.example;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by amarendra on 14/04/17.
 */
public interface QueryStream {
    public <T> T streamQuery(String sql, Function<Stream<SqlRowSet>, ? extends T> streamer, Object... args);
}

