package com.project.doodle.features;

import com.project.doodle.models.Choice;
import com.project.doodle.models.Poll;
import com.project.doodle.models.User;
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
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.*;


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
    private static final String EXCEL_FILE_LOCATION = "./generatedFiles";


    @RequestMapping(value = "/polls/{slug}/results", method = RequestMethod.GET, produces = APPLICATION_EXCEL)
    public @ResponseBody HttpEntity<byte[]> downloadResultsExcel(@PathVariable String slug) throws IOException {
        Optional<Poll> poll = pollRepository.findBySlug(slug);
        if (!poll.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        String filePath = createExcelFile(poll.get(),slug);
        return getHttpEntityToDownload(filePath,"vnd.ms-excel");
    }

    @RequestMapping(value = "/polls/{slug}/print", method = RequestMethod.GET, produces = APPLICATION_PDF)
    public @ResponseBody HttpEntity<byte[]> downloadResultsPdf(@PathVariable String slug) throws IOException {
        Optional<Poll> poll = pollRepository.findBySlug(slug);
        if (!poll.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        String filePath = "./Test.pdf";
        return getHttpEntityToDownload(filePath,"pdf");
    }

    private String convertToPdf(String filePath){
        return "";
    }

    static int beginningColumnCell = 0;
    static int beginningRowCell = 3;
    private String createExcelFile(Poll poll,String slug) throws IOException{
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy-HH.mm.ss");
        Date date = new Date();
        String fileName = EXCEL_FILE_LOCATION+"/"+slug+"-"+dateFormat.format(date)+".xls";

       // Create an Excel file
       WritableWorkbook Wbook = null;
       try {
           // Create an Excel file in the file location
           Wbook = Workbook.createWorkbook(new File(fileName));

           // Create an Excel sheet
           WritableSheet mainSheet = Wbook.createSheet("SONDAGE", 0);

           // Format objects
           WritableCellFormat cFormat = new WritableCellFormat();
           WritableFont font = new WritableFont(WritableFont.ARIAL, 16, WritableFont.BOLD);
           cFormat.setFont(font);

           Label label;
           label = new Label(0, 0, "Sondage \""+poll.getTitle()+"\"", cFormat);
           mainSheet.addCell(label);
           label = new Label(0, 1, "http://localhost:3000/polls/"+poll.getSlug());
           mainSheet.addCell(label);

           // On récupere les users qui ont voté dans ce sondage
           List<User> users = retrieveUsers(poll);

           // On ecrit les users sur la première colonne
           writeUsers(poll, Wbook, users);

           // On ecrit les choix avec les votes de chaque users
           writeChoices(poll,Wbook,users);

           // On ecrit les donnée du workbook dans un format excel
           Wbook.write();

       } catch (WriteException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       } finally {

           if (Wbook != null) {
               try {
                   Wbook.close();
               } catch (IOException e) {
                   e.printStackTrace();
               } catch (WriteException e) {
                   e.printStackTrace();
               }
           }


       }

        return fileName;
    }

    private  List<User> retrieveUsers (Poll poll) {
       List<User> users = new ArrayList<>();
       // On parcours les choix du poll pour récupérer les users ayant voté
       if (!poll.getPollChoices().isEmpty()) {
           for (Choice choice : poll.getPollChoices()) {
               if (!choice.getUsers().isEmpty()) {
                   for (User user : choice.getUsers()) {
                       // On vérifie que le user ne soit pas déjà dans la liste
                       if (!users.contains(user)) {
                           users.add(user);
                       }
                   }
               }
           }
       }
       return users;
   }

    private void writeChoices(Poll poll, WritableWorkbook Wbook, List<User> users) throws jxl.write.WriteException{
        Label label;
        Number number;
        WritableSheet mainSheet = Wbook.getSheet(0);

        List<Choice> choices = poll.getPollChoices();

        // On ecrit les colonnes des choix
        for (int i=0; i<choices.size();i++){
            // On ecrit la date
            writeChoiceDate(Wbook,choices,i);

            // On ecrit les votes
            List<User> listUsersVotes = choices.get(i).getUsers();
            for (int x=0;x<users.size();x++){
                if(listUsersVotes.contains(users.get(x))){
                    label = new Label(1+beginningColumnCell+i, 3+beginningRowCell+x, "OK");
                }else{
                    label = new Label(1+beginningColumnCell+i, 3+beginningRowCell+x, "-");
                }
                mainSheet.addCell(label);
            }
            // on ecrit le nombre total de vote pour le choix
            number = new Number(1+beginningColumnCell+i, 3+beginningRowCell+users.size(), listUsersVotes.size());
            mainSheet.addCell(number);

        }
    }

    private void writeChoiceDate(WritableWorkbook Wbook,List<Choice> choices,int i) throws jxl.write.WriteException{
       Label label;
       WritableSheet mainSheet = Wbook.getSheet(0);
       String month[] = {"Janvier","Février","Mars","Avril","Mai","Juin","Juillet","Aout","Septembre","Novembre","Décembre"};
       String dayOfWeek[] = {"Lundi","Mardi","Mercredi","Jeudi","Vendredi","Samedi","Dimanche"};

       // On recupère la date de début
       Choice choice=choices.get(i);
       Date startDate = choice.getstartDate();
       Calendar calendar = Calendar.getInstance();
       calendar.setTime(startDate);
       int startYear = calendar.get(Calendar.YEAR);
       String startMonth = month[calendar.get(Calendar.MONTH)];
       int startDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
       String startDayOfWeek = dayOfWeek[calendar.get(Calendar.DAY_OF_WEEK)-1];
       int startHourInt = calendar.get(Calendar.HOUR_OF_DAY);
       String startHour = (startHourInt<10?"0":"")+startHourInt;
       int startMinuteInt = calendar.get(Calendar.MINUTE);
       String startMinute = (startMinuteInt<10?"0":"")+startMinuteInt;
       // On recupère la date de fin
       Date endDate = choice.getendDate();
       calendar.setTime(endDate);
       int endHourInt = calendar.get(Calendar.HOUR_OF_DAY);
       String endHour = (endHourInt<10?"0":"")+endHourInt;
       int endMinuteInt = calendar.get(Calendar.MINUTE);
       String endMinute = (endMinuteInt<10?"0":"")+endMinuteInt;

       label = new Label(1+beginningColumnCell+i, beginningRowCell, startMonth+" "+startYear );
       mainSheet.addCell(label);
       label = new Label(1+beginningColumnCell+i, 1+beginningRowCell, startDayOfWeek+" "+startDayOfMonth );
       mainSheet.addCell(label);
       label = new Label(1+beginningColumnCell+i, 2+beginningRowCell, startHour+":"+startMinute+" - "+endHour+":"+endMinute );
       mainSheet.addCell(label);
   }

    private void writeUsers(Poll poll,WritableWorkbook Wbook, List<User> users) throws jxl.write.WriteException {
        Label label;

        WritableSheet mainSheet = Wbook.getSheet(0);
        // On ecrit la premier colonne avec users et label "Nombre"
        for (int i=0; i<users.size();i++){
            label = new Label(beginningColumnCell, 3+beginningRowCell+i, users.get(i).getUsername());
            mainSheet.addCell(label);
        }
        label = new Label(beginningColumnCell, 3+beginningRowCell+users.size(), "Nombre");
        mainSheet.addCell(label);
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
