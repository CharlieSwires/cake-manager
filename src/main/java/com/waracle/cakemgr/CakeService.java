package com.waracle.cakemgr;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CakeService {

    @Autowired
    private CakeRepo repo;
    
    public Boolean init() throws IOException {

        System.out.println("init started");
        
        final String uri = "https://gist.githubusercontent.com/hart88/198f29ec5114a3ec3460/raw/8dd19a88f9b8d24c23d9960f3300d0c917a4f07c/cake.json";
        RestTemplate restTemplate = new RestTemplate(); 
        String jsonAsString = restTemplate.getForObject( uri, String.class);
        ObjectMapper mapper = new ObjectMapper();

        InputBean[] ibs = mapper.readValue(jsonAsString, InputBean[].class);

        for(InputBean item : ibs) {
            CakeEntity ce = new CakeEntity(item.getTitle(), item.getDesc(), item.getImage());
            try {
                repo.save(ce);
                System.out.println("adding cake entity");
            } catch (Exception e) {
                continue;
            }
        }
        System.out.println("init finished");
        return true;
    }

    public Boolean add(InputBean ib) {
        assert(ib != null);
        assert(ib.getTitle() != null && !(ib.getTitle().trim().isEmpty()));
        assert(ib.getDesc() != null && !(ib.getDesc().trim().isEmpty()));
        assert(ib.getImage() != null && !(ib.getImage().trim().isEmpty()));
        CakeEntity ce = new CakeEntity(ib.getTitle(), ib.getDesc(), ib.getImage());
        try{
            repo.save(ce);
        } catch (ConstraintViolationException e) {
            return false;
        }
        return true;
        
    }
    public String getCakesCSV() throws IOException {

        List<CakeEntity> list = repo.findAll();
        Collections.sort(list, new Comparator<CakeEntity>() {

            @Override
            public int compare(CakeEntity mb1, CakeEntity mb2) {
                if (mb1 == null || mb2 == null) return 0;
                String dmb1 = mb1.getTitle() != null ? mb1.getTitle() : "";
                String dmb2 = mb2.getTitle() != null ? mb2.getTitle() : "";
                int comp = dmb1.compareToIgnoreCase(dmb2);
               
                return comp;
            }
        });

        try (
                BufferedWriter writer = Files.newBufferedWriter(Paths.get("cakes.csv"));

                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                        .withHeader("Title","Description","Image"));
                ) {
            for(CakeEntity mb: list) {
                csvPrinter.printRecord(mb.getTitle(),
                        mb.getDescription(),
                        mb.getImage());

            }

            csvPrinter.flush();            
        }    
        return "cakes.csv";
    }
    public List<CakeEntity> getCakesJSON() {

        List<CakeEntity> list = repo.findAll();
        Collections.sort(list, new Comparator<CakeEntity>() {

            @Override
            public int compare(CakeEntity mb1, CakeEntity mb2) {
                if (mb1 == null || mb2 == null) return 0;
                String dmb1 = mb1.getTitle() != null ? mb1.getTitle() : "";
                String dmb2 = mb2.getTitle() != null ? mb2.getTitle() : "";
                int comp = dmb1.compareToIgnoreCase(dmb2);
               
                return comp;
            }
        });

        return list;

    }

}
