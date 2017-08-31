package pl.com.tt.ttime.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.com.tt.ttime.exception.RestRequestException;
import pl.com.tt.ttime.rest.JiraRestService;
import pl.com.tt.ttime.rest.model.Project;

import java.util.List;

@RequestMapping(value = "/project")
@RestController
public class ProjectController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectController.class);

    @Autowired
    private JiraRestService jiraRestService;

    @Value("${ttimemanager.jira.reports.enabled}")
    private boolean jiraReportsEnabled;

    @Secured({"ROLE_ADMIN"})
    @GetMapping
    public ResponseEntity<List<Project>> listAllProjects() {

        if(!jiraReportsEnabled){
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }

        List<Project> projects = null;

        try {
            projects = jiraRestService.getProjects();
        } catch (RestRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(projects, HttpStatus.OK);
    }
}
