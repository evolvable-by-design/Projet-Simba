package com.project.doodle.features;

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

import jxl.Workbook;
import jxl.write.*;
import jxl.write.Number;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Optional;


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

    private static final String APPLICATION_EXCEL = "application/vnd.ms-excel";
    private static final String APPLICATION_PDF = "application/pdf";
    private static final String EXCEL_FILE_LOCATION = "./generatedFiles/MyFirstExcel.xls";


    @RequestMapping(value = "/polls/{slug}/results", method = RequestMethod.GET, produces = APPLICATION_EXCEL)
    public @ResponseBody HttpEntity<byte[]> downloadResultsExcel(@PathVariable String slug) throws IOException {
        createExcelFile(slug);
        String filePath = "./Test.xlsx";
        return getHttpEntityToDownload(filePath,"vnd.ms-excel");
    }
    @RequestMapping(value = "/polls/{slug}/print", method = RequestMethod.GET, produces = APPLICATION_PDF)
    public @ResponseBody HttpEntity<byte[]> downloadResultsPdf() throws IOException {
        String filePath = "./Test.pdf";
        return getHttpEntityToDownload(filePath,"pdf");
    }

   private void createExcelFile(String slug){

       Optional<Poll> poll = pollRepository.findBySlug(slug);
       if (!poll.isPresent()) {
           return ;
       }
       // Create an Excel file
       WritableWorkbook myFirstWbook = null;
       try {
           // Create an Excel file in the file location
           myFirstWbook = Workbook.createWorkbook(new File(EXCEL_FILE_LOCATION));
           // Create an Excel sheet
           WritableSheet myFirstSheet = myFirstWbook.createSheet("SONDAGE", 0);

           WritableCellFormat cFormat = new WritableCellFormat();
           WritableFont font = new WritableFont(WritableFont.ARIAL, 16, WritableFont.BOLD);
           cFormat.setFont(font);
           Label label;
           Number number;


           label = new Label(0, 0, "Sondage \""+"title"+"\"", cFormat);
           myFirstSheet.addCell(label);
           number = new Number(0, 1, 1);
           myFirstSheet.addCell(number);

           label = new Label(1, 0, "Result", cFormat);
           myFirstSheet.addCell(label);
           label = new Label(1, 1, "Passed");
           myFirstSheet.addCell(label);

           number = new Number(0, 2, 2);
           myFirstSheet.addCell(number);

           label = new Label(1, 2, "Passed 2");
           myFirstSheet.addCell(label);

           // Writes out the data held in this workbook in Excel format
           myFirstWbook.write();

       } catch (WriteException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       } finally {

           if (myFirstWbook != null) {
               try {
                   myFirstWbook.close();
               } catch (IOException e) {
                   e.printStackTrace();
               } catch (WriteException e) {
                   e.printStackTrace();
               }
           }


       }


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
