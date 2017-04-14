package com.example;

import org.fluttercode.datafactory.impl.DataFactory;

import java.io.FileWriter;
import java.util.*;

/**
 * Created by amarendra on 14/04/17.
 */
public class CSVGenerator {


    private static DataFactory df = new DataFactory();

    public static void main(String[] args) throws Exception {

        String csvFile = "/Users/amarendra/IdeaProjects/SpringBootPhoenix/src/main/resources/sample.csv";
        FileWriter writer = new FileWriter(csvFile, true);

        CSVGenerator csvGenerator = new CSVGenerator();
        for (int i = 0; i < 100000; i++) {
            WebStat webStat = new WebStat();
            webStat.setHost(csvGenerator.selectHost())
                    .setDomain(csvGenerator.selectDomain())
                    .setDate(csvGenerator.selectDate())
                    .setFeature(csvGenerator.selectFeature())
                    .setActiveVisitor(df.getNumberUpTo(999))
                    .setDb(df.getNumberUpTo(999))
                    .setCore(df.getNumberUpTo(999));
            List<String> list = new ArrayList<>();
            list.add(webStat.getHost());
            list.add(webStat.getDomain());
            list.add(webStat.getFeature());
            list.add(webStat.getDate().toString());
            list.add(String.valueOf(webStat.getActiveVisitor()));
            list.add(String.valueOf(webStat.getDb()));
            list.add(String.valueOf(webStat.getCore()));
            CSVUtils.writeLine(writer, list);
        }
;
        writer.flush();
        writer.close();

    }

    private String selectHost(){
        String[] hosts = {"NA","EU","AU","IN"};
        int idx = new Random().nextInt(hosts.length);
        String random = (hosts[idx]);
        return random;
    }

    private String selectDomain(){
        String[] domains = {"Salesforce.com","Apple.com","google.com","facebook.com","yahoo.com","microsoft.com"};
        int idx = new Random().nextInt(domains.length);
        String random = (domains[idx]);
        return random;
    }

    private String selectFeature(){
        String[] feature = {"Login","Reports","Dashboard","Mac","Ipad","Search"};
        int idx = new Random().nextInt(feature.length);
        String random = (feature[idx]);
        return random;
    }

    private java.sql.Date selectDate(){
        Date minDate = df.getDate(2000, 1, 1);
        Date maxDate = new Date();
        java.sql.Date date = new java.sql.Date(df.getDateBetween(minDate,maxDate).getTime());
        return date;
    }
}
