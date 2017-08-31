package pl.com.tt.ttime.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.com.tt.ttime.error.ErrorInformation;
import pl.com.tt.ttime.exception.RestRequestException;
import pl.com.tt.ttime.model.Report;
import pl.com.tt.ttime.rest.JiraRestService;
import pl.com.tt.ttime.rest.model.CSVReportEntry;
import pl.com.tt.ttime.service.ReportService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping(value = "/report")
public class ReportController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportController.class);

    private ReportService service;
    private JiraRestService jiraRestService;

    @Autowired
    public void setJiraRestService(JiraRestService jiraRestService) {
        this.jiraRestService = jiraRestService;
    }

    @Autowired
    public void setService(ReportService service) {
        this.service = service;
    }

    @Secured({"ROLE_ADMIN"})
    @GetMapping(value = "/generate", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public @ResponseBody ResponseEntity<byte[]> generateExcelXls(@RequestParam("startDate") String startDate,
                                                                 @RequestParam("endDate") String endDate,
                                                                 @RequestParam("projectId") Integer[] projectIds,
                                                                 @RequestParam(value = "userName", required = false) List<String> userName,
                                                                 HttpServletResponse response)
    throws IOException, URISyntaxException{

        if(!service.checkIfJiraReportsEnabled()){
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }

        List<CSVReportEntry> reportCSV;
        byte[] newXlsFile;
        try {
            reportCSV = jiraRestService.getUsersReportCSV(LocalDate.parse(startDate),
                    LocalDate.parse(endDate), projectIds,
                    userName);
        } catch (RestRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        newXlsFile = service.generateNewFileReport(reportCSV);
        response.setHeader("Content-Disposition", "attachment;filename=report.xlsx");
        return new ResponseEntity<>(newXlsFile, HttpStatus.OK);
    }

    @Secured({"ROLE_ADMIN"})
    @PostMapping
    public @ResponseBody ResponseEntity<?> save(@RequestParam("file") MultipartFile file) {

        if(!service.checkIfJiraReportsEnabled()){
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }

        try {
            return new ResponseEntity<>(service.saveFile(file), HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ErrorInformation("No found any users in project or our database or file is empty"),
                    HttpStatus.NOT_ACCEPTABLE);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ErrorInformation("Something wrong while read file. Maybe you upload wrong file format?"),
                    HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping
    public @ResponseBody ResponseEntity<?> sendListAllUserReports() {
        if(!service.checkIfJiraReportsEnabled()){
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }
        return new ResponseEntity<>(service.getLoggedUserReports(), HttpStatus.OK);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping(value = "/{id}", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public @ResponseBody ResponseEntity<?> getReport(@PathVariable Long id, HttpServletResponse response) {

        if(!service.checkIfJiraReportsEnabled()){
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }

        byte[] fileCSV;
        Report report;
        try {
            report = service.findById(id);
            fileCSV = service.generateFileFromReport(report);
        } catch (IOException|URISyntaxException e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ErrorInformation("Something wrong with our database"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.setHeader("Content-Disposition", "attachment;filename="+report.getFileName());
        return new ResponseEntity<>(fileCSV, HttpStatus.OK);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping(value = "/enabled")
    public @ResponseBody Boolean getValueOfJiraReportsEnabled() {
        return service.checkIfJiraReportsEnabled();
    }
}
