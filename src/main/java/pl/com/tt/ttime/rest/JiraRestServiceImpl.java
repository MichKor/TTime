package pl.com.tt.ttime.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pl.com.tt.ttime.exception.RestRequestException;
import pl.com.tt.ttime.rest.model.CSVReportEntry;
import pl.com.tt.ttime.rest.model.Project;
import pl.com.tt.ttime.rest.model.TimesheetReport;
import pl.com.tt.ttime.rest.model.TimesheetReportToCSVMapper;
import pl.com.tt.ttime.service.ExcelService;

import java.io.*;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class JiraRestServiceImpl implements JiraRestService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JiraRestServiceImpl.class);

    private static final String REPORT_URL_FORMAT = "rest/timesheet-gadget/1.0/raw-timesheet.json?" +
            "moreFields=reporter" +
            "&moreFields=status" +
            "&moreFields=timespent" +
            "&moreFields=project" +
            "&moreFields=issuetype" +
            "&moreFields=team" +
            "&moreFields=customfield_11800" + //TODO: Pole Epic Link wyciągać dynamicznie z JIRY
            "&startDate=%s" +
            "&endDate=%s";
    private static final String PROJECT_ID = "&projectid=%d";
    private static final String TARGET_USER = "&targetUser=";
    private static final String TARGET_GROUP_ANY = "&targetGroup=@any";
    private static final String PROJECTS_URL = "rest/api/2/project/";

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Value("${ttimemanager.jira.url}")
    private String jiraBaseUrl;

    private RestTemplate template;

    @Autowired
    public JiraRestServiceImpl(RestTemplate template) {
        this.template = template;
    }

    private <T> ResponseEntity<T> getRequest(String url, Class<T> targetClass) throws HttpClientErrorException {
        return template.exchange(jiraBaseUrl + url, HttpMethod.GET, null, targetClass);
    }

    @Override
    public List<CSVReportEntry> getUsersReportCSV(LocalDate dateStart, LocalDate dateEnd, Integer[] projectId, List<String> userName) throws RestRequestException {
        StringBuilder formattedUrl = new StringBuilder(String.format(REPORT_URL_FORMAT, dateStart.format(dateTimeFormatter), dateEnd.format(dateTimeFormatter)));
        for (Integer id : projectId) {
            formattedUrl.append(String.format(PROJECT_ID, id));
        }
        if (userName == null || userName.size() == 0) {
            formattedUrl.append(TARGET_GROUP_ANY);
        }
        else {
            ListIterator userIterator = userName.listIterator();
            formattedUrl.append(TARGET_USER).append(userIterator.next());
            while (userIterator.hasNext()) formattedUrl.append(",").append(userIterator.next());
        }

        String result = formattedUrl.toString();
        ResponseEntity<TimesheetReport> responseEntity;
        try {
            responseEntity = getRequest(result, TimesheetReport.class);
        } catch (HttpClientErrorException ex) {
            throw new RestRequestException("Response returned with an error code.", ex);
        }
        List<CSVReportEntry> reportEntries = new ArrayList<>();
        reportEntries.addAll(TimesheetReportToCSVMapper.mapToCsvReport(responseEntity.getBody()));
        return reportEntries;
    }

    @Override
    public List<Project> getProjects() throws RestRequestException {
        ResponseEntity<Project[]> response;
        try {
            response = getRequest(PROJECTS_URL, Project[].class);
        } catch (HttpClientErrorException ex) {
            throw new RestRequestException("Response returned with an error code.", ex);
        }
        return Arrays.asList(response.getBody());
    }
}
