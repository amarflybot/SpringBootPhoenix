package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

/**
 * Created by amarendra on 14/04/17.
 */
@RestController
@RequestMapping("/mvcapi")
public class WebStatResourceMVC {

    private static Logger logger = LoggerFactory.getLogger(WebStatResourceMVC.class);

    @Autowired
    private WebStatDao webStatDao;


    @GetMapping("/getAll")
    @ResponseBody
    public void getSomething(HttpServletResponse response) throws SQLException, IOException {
        logger.info("new Request came to Get All!");
        PrintWriter writer = response.getWriter();
        webStatDao.getStreamingOutputForSql("SELECT * FROM WEB_STAT", new Object[]{}, writer);
    }

    @GetMapping("/getByDomain/{domain}")
    @ResponseBody
    public void getByDomain(@PathVariable String domain, HttpServletResponse response) throws SQLException, IOException {
        logger.info("new Request came to getByDomain!");
        PrintWriter writer = response.getWriter();
        webStatDao.getStreamingOutputForSql("SELECT * FROM WEB_STAT where DOMAIN = ?", new Object[]{domain+".com"}, writer);
    }

    @GetMapping("//getByHost/{host}")
    @ResponseBody
    public void getByHost(@PathVariable String host,  HttpServletResponse response) throws SQLException, IOException {
        logger.info("new Request came to getByHost!");
        PrintWriter writer = response.getWriter();
        webStatDao.getStreamingOutputForSql("SELECT * FROM WEB_STAT where HOST = ?", new Object[]{host}, writer);
    }

}
