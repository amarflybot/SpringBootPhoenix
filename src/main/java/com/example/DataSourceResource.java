package com.example;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.ResultSetWrappingSqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
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
@Path("/api")
public class DataSourceResource {

    private static Logger logger = LoggerFactory.getLogger(DataSourceResource.class);


    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Gson gson = new Gson();

    @GET
    @Path("/getAll")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSomething() {
        logger.info("new Request came to get All!");
        StreamingOutput stream = getStreamingOutput("SELECT * FROM WEB_STAT", new Object[]{});
        return Response.ok(stream).build();
    }

    @GET
    @Path("/getByDomain/{domain}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByDomain(@PathParam("domain") String domain) {
        logger.info("new Request came to Get By Domain!");
        StreamingOutput stream = getStreamingOutput("SELECT * FROM WEB_STAT where DOMAIN = ?", new Object[]{domain});
        return Response.ok(stream).build();
    }

    @GET
    @Path("/getByHost/{host}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByHost(@PathParam("host") String host) {
        logger.info("new Request came to Get By host!");
        StreamingOutput stream = getStreamingOutput("SELECT * FROM WEB_STAT where HOST = ?", new Object[]{host});
        return Response.ok(stream).build();
    }

    private StreamingOutput getStreamingOutput(final String sql, final Object[] args) {
        return new StreamingOutput() {
                @Override
                public void write(OutputStream os) throws IOException, WebApplicationException {
                    Writer writer = new BufferedWriter(new OutputStreamWriter(os));
                    jdbcTemplate.query(
                            sql, args,
                            (rs, rowNum) -> new WebStat().setHost(rs.getString("HOST"))
                                    .setDomain(rs.getString("DOMAIN"))
                                    .setFeature(rs.getString("FEATURE"))
                                    .setDate(rs.getDate("DATE"))
                                    .setCore(rs.getInt("CORE"))
                                    .setDb(rs.getInt("DB"))
                                    .setActiveVisitor(rs.getInt("ACTIVE_VISITOR"))
                    ).forEach(webStat -> {
                        try {
                            writer.write(gson.toJson(webStat));
                            writer.flush();
                            //TimeUnit.MILLISECONDS.sleep(500);
                        } catch (IOException e) {
                            throw new RuntimeException("Cannot write to Stream back");
                        }
                    });
                }
            };
    }

    public <T> T streamQuery(String sql, Function<Stream<SqlRowSet>, ? extends T> streamer, Object... args) {
        return jdbcTemplate.query(sql, resultSet -> {
            final SqlRowSet rowSet = new ResultSetWrappingSqlRowSet(resultSet);
            final boolean parallel = false;

            // The ResultSet API has a slight impedance mismatch with Iterators, so this conditional
            // simply returns an empty iterator if there are no results
            if (!rowSet.next()) {
                return streamer.apply(StreamSupport.stream(Spliterators.emptySpliterator(), parallel));
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
                    first = false; // iterators can be unwieldy sometimes
                    return rowSet;
                }
            }, Spliterator.IMMUTABLE);
            return streamer.apply(StreamSupport.stream(spliterator, parallel));
        }, args);
    }

}
