package com.waracle.cakemgr;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;


@RestController
@RequestMapping(path = "/")
public class RController  {

    @Autowired
    private CakeService service;
    @Autowired
    private FileStorageService fileStorageService;
    
    @PostMapping(path="cake", produces="application/json", consumes="application/json")
    public ResponseEntity<Boolean> postFile(@RequestBody InputBean input) {
        return new ResponseEntity<Boolean>(service.add(input), HttpStatus.OK);
    }
    @GetMapping(path="init", produces="application/json")
    public ResponseEntity<Boolean> init() throws IOException {
        return new ResponseEntity<Boolean>(service.init(), HttpStatus.OK);
    }
    @GetMapping(path="", produces="application/json")
    public ResponseEntity<Resource> getFileCSV(HttpServletRequest request) throws IOException, Exception {
        return downloadFile(service.getCakesCSV(), request);
    }
    @GetMapping(path="cake", produces="application/json")
    public ResponseEntity<List<CakeEntity>> getFileJSON(HttpServletRequest request) {
        return new ResponseEntity<List<CakeEntity>>(service.getCakesJSON(), HttpStatus.OK);
    }

    private ResponseEntity<Resource> downloadFile(String filename, HttpServletRequest request) throws Exception {
        
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(filename);
 
        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
