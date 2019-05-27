package com.project.doodle.controllers;

import com.project.doodle.models.Poll;
import com.project.doodle.repositories.ChoiceRepository;
import com.project.doodle.repositories.PollRepository;
import com.project.doodle.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.*;


@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api")
public class ExportResource {
    @Autowired
    private ChoiceRepository choiceRepository;
    @Autowired
    private PollRepository pollRepository;
    @Autowired
    private UserRepository userRepository;

    private static final String APPLICATION_PDF = "application/pdf";
    private static final String APPLICATION_EXCEL = "application/vnd.ms-excel";

    @RequestMapping(value = "/polls/{idPoll}/print", method = RequestMethod.GET, produces = APPLICATION_PDF)
    public @ResponseBody HttpEntity<byte[]> downloadResultsPdf() throws IOException {
        String filePath = "./Test.pdf";
        return getHttpEntityToDownload(filePath,"pdf");
    }

    @RequestMapping(value = "/polls/{idPoll}/results", method = RequestMethod.GET, produces = APPLICATION_EXCEL)
    public @ResponseBody HttpEntity<byte[]> downloadResultsExcel() throws IOException {
        String filePath = "./Test.xlsx";
        return getHttpEntityToDownload(filePath,"vnd.ms-excel");
    }




    private HttpEntity<byte[]> getHttpEntityToDownload(String filePath,String fileType) throws IOException {
        File file = getFile(filePath);
        byte[] document = FileCopyUtils.copyToByteArray(file);

        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", fileType));
        header.set("Content-Disposition", "inline; filename=" + file.getName());
        header.setContentLength(document.length);

        return new HttpEntity<>(document, header);
    }

    private File getFile(String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        if (!file.exists()){
            throw new FileNotFoundException("file with path: " + filePath + " was not found.");
        }
        return file;
    }

    @ControllerAdvice
    public class GlobalExceptionHandler {

        @ExceptionHandler(value = FileNotFoundException.class)
        public void handle(FileNotFoundException ex, HttpServletResponse response) throws IOException {
            System.out.println("handling file not found exception");
            response.sendError(404, ex.getMessage());
        }

        @ExceptionHandler(value = IOException.class)
        public void handle(IOException ex, HttpServletResponse response) throws IOException {
            System.out.println("handling io exception");
            response.sendError(500, ex.getMessage());
        }
    }


}
