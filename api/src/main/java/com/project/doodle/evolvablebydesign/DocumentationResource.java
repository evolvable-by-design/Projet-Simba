package com.project.doodle.evolvablebydesign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URL;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api")
public class DocumentationResource {

    @RequestMapping(method=RequestMethod.OPTIONS)
    public ResponseEntity<String> getOpenApiDocumentation() {
        try {
            URL resource = this.getClass().getClassLoader().getResource("openapi.yaml");

            if (resource == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
                Object yaml = yamlReader.readValue(resource, Object.class);

                ObjectMapper jsonWriter = new ObjectMapper();
                String documentation = jsonWriter.writeValueAsString(yaml);

                return createJsonResponse(documentation);
            }
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private <T> ResponseEntity<T> createJsonResponse(T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }

}
