package com.example;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.sql.SQLException;
import java.util.function.BinaryOperator;
import java.util.function.Function;

/**
 * Created by amarendra on 14/04/17.
 */
@Path("/api")
public class DataSourceResource {

    private static Logger logger = LoggerFactory.getLogger(DataSourceResource.class);


    @Autowired
    private JdbcStream jdbcStream;

    private Gson gson = new Gson();

    @GET
    @Path("/getAll")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSomething() throws SQLException {
        logger.info("new Request came to get All!");
        JdbcStream.StreamableQuery streamableQuery = jdbcStream.streamableQuery("SELECT * FROM WEB_STAT", new Object[]{});
        StreamingOutput streamingOutput = getStreamingOutput(streamableQuery);
        return Response.ok(streamingOutput).build();
    }

    /*@GET
    @Path("/getByDomain/{domain}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByDomain(@PathParam("domain") String domain) {
        logger.info("new Request came to Get By Domain!");
        StreamingOutput stream = getStreamingOutput("SELECT * FROM WEB_STAT where DOMAIN = ?", new Object[]{domain});
        return Response.ok(stream).build();
    }*/

    /*@GET
    @Path("/getByHost/{host}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByHost(@PathParam("host") String host) {
        logger.info("new Request came to Get By host!");
        StreamingOutput stream = getStreamingOutput("SELECT * FROM WEB_STAT where HOST = ?", new Object[]{host});
        return Response.ok(stream).build();
    }*/

    private StreamingOutput getStreamingOutput(final JdbcStream.StreamableQuery streamableQuery) {
        return new StreamingOutput() {
                @Override
                public void write(OutputStream os) throws IOException, WebApplicationException {
                    Writer writer = new BufferedWriter(new OutputStreamWriter(os));
                    try {
                        streamableQuery
                                .stream()
                                .map(new Function<JdbcStream.SqlRow, WebStat>() {
                                    @Override
                                    public WebStat apply(JdbcStream.SqlRow sqlRow) {
                                        try {
                                            WebStat webStat = WebStatMapper.mapWebStat(sqlRow);
                                            return webStat;
                                        } catch (RuntimeException e) {
                                            throw new RuntimeException("Cannot convert SqlRom to WebStat");
                                        }
                                    }
                                }).reduce(new BinaryOperator<WebStat>() {
                            @Override
                            public WebStat apply(WebStat o, WebStat o2) {
                                Assert.notNull(o2, "Webstat cannot be null");
                                try {
                                    writer.write(gson.toJson(o2));
                                    writer.flush();
                                    //TimeUnit.MILLISECONDS.sleep(500);
                                } catch (IOException e) {
                                    throw new RuntimeException("Cannot write to Stream back");
                                }
                                return o;
                            }
                        });
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    /*jdbcTemplate.query(
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

                    rs -> {
                                    new WebStat().setHost(rs.getString("HOST"))
                                            .setDomain(rs.getString("DOMAIN"))
                                            .setFeature(rs.getString("FEATURE"))
                                            .setDate(new Date(rs.getTimestamp("DATE").getTime()))
                                            .setCore(Integer.valueOf(rs.getString("CORE")))
                                            .setDb(Integer.valueOf(rs.getString("DB")))
                                            .setActiveVisitor(Integer.valueOf(rs.getString("ACTIVE_VISITOR")))
                                })
                    */
                }
            };
    }

}
