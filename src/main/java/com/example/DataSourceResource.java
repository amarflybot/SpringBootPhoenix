package com.example;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import javax.ws.rs.*;
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

    @GET
    @Path("/getByDomain/{domain}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByDomain(@PathParam("domain") String domain) throws SQLException {
        logger.info("new Request came to Get By Domain!");
        JdbcStream.StreamableQuery streamableQuery = jdbcStream.streamableQuery("SELECT * FROM WEB_STAT where DOMAIN = ?", new Object[]{domain});
        StreamingOutput streamingOutput = getStreamingOutput(streamableQuery);
        return Response.ok(streamingOutput).build();
    }

    @GET
    @Path("/getByHost/{host}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByHost(@PathParam("host") String host) throws SQLException {
        logger.info("new Request came to Get By host!");
        JdbcStream.StreamableQuery streamableQuery = jdbcStream.streamableQuery("SELECT * FROM WEB_STAT where HOST = ?", new Object[]{host});
        StreamingOutput streamingOutput = getStreamingOutput(streamableQuery);
        return Response.ok(streamingOutput).build();
    }

    private StreamingOutput getStreamingOutput(final JdbcStream.StreamableQuery streamableQuery) {
        return new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                Writer writer = new BufferedWriter(new OutputStreamWriter(os));
                writer.write("[");
                final boolean[] first = {false};
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
                                if (first[0]) {
                                    writer.write(",");
                                }
                                first[0] = true;
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
                writer.write("]");
                writer.flush();
            }
        };
    }

}
