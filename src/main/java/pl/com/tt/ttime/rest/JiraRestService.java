package pl.com.tt.ttime.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import pl.com.tt.ttime.exception.RestRequestException;
import pl.com.tt.ttime.rest.model.CSVReportEntry;
import pl.com.tt.ttime.rest.model.Project;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface JiraRestService {
    List<CSVReportEntry> getUsersReportCSV(LocalDate dateStart, LocalDate dateEnd, Integer[] projectId, List<String> userName)
            throws RestRequestException;

    List<Project> getProjects() throws RestRequestException;
}
