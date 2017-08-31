package pl.com.tt.ttime.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.com.tt.ttime.controller.UserController;
import pl.com.tt.ttime.model.Report;
import pl.com.tt.ttime.model.User;
import pl.com.tt.ttime.repository.ReportRepository;
import pl.com.tt.ttime.rest.model.CSVReportEntry;
import pl.com.tt.ttime.security.SecurityUtils;
import pl.com.tt.ttime.service.ExcelService;
import pl.com.tt.ttime.service.ReportService;
import pl.com.tt.ttime.service.UserService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ReportServiceImpl extends AbstractServiceImpl<Report, Long, ReportRepository> implements ReportService {

    private UserService userService;
    private ExcelService excelService;

    @Value("${ttimemanager.jira.reports.enabled}")
    private boolean jiraReportsEnabled;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setExcelService(ExcelService excelService) {
        this.excelService = excelService;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public ReportServiceImpl(ReportRepository repository) {
        super(repository);
    }

    @Override
    public byte[] generateNewFileReport(List<CSVReportEntry> reportCSV) throws IOException, URISyntaxException {
        return excelService.reportToBytes(reportCSV);
    }

    @Override
    public Boolean checkIfJiraReportsEnabled() {
        return jiraReportsEnabled;
    }

    @Override
    public Report saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            LOGGER.info("File is empty");
            throw new IllegalStateException("File is empty");
        }
        List<CSVReportEntry> entryList = excelService.readReport(file.getInputStream());
        Set<String> authors = new HashSet<>();
        Set<String> projects = new HashSet<>();
        for (CSVReportEntry entry : entryList) {
            authors.add(entry.getAuthor());
            projects.add(entry.getProject());
        }
        if (authors.isEmpty()) {
            LOGGER.info("No found any user in uploading file");
            throw new IllegalStateException("No found any user in uploading file");
        }
        Report report = new Report();
        report.addUsersToReport(userService.findAllByUsername(authors));
        if (report.getUsers().isEmpty()) {
            LOGGER.info("No found any user in database or ldap");
            throw new IllegalStateException("No found any user in database");
        }
        report.setUploadTimestamp(LocalDateTime.now());
        //TODO: odpytać JIRE, czy te projekty istnieją? Plik jest przecież po modyfikacji
        report.setFileName(projects);
        report.setReportFile(file.getBytes());
        return repository.save(report);
    }

    @Override
    public byte[] generateFileFromReport(Report reportUser) throws IOException, URISyntaxException {
        User loggedUser = SecurityUtils.getCurrentUser(userService);
        Collection<? extends GrantedAuthority> authorities = loggedUser.getAuthorities();
        if (userIsAdmin(authorities)) {
            return reportUser.getReportFile();
        }

        List<CSVReportEntry> reportEntries = excelService.readReport(new ByteArrayInputStream(reportUser.getReportFile()));
        List<CSVReportEntry> rowsLoggedUser = new ArrayList<>();
        for (CSVReportEntry row : reportEntries) {
            if (loggedUser.getUsername().equals(row.getAuthor())) {
                rowsLoggedUser.add(row);
            }
        }

        return excelService.reportToBytes(rowsLoggedUser);
    }

    @Override
    public Collection<Report> getLoggedUserReports() {
        User loggedUser = SecurityUtils.getCurrentUser(userService);
        Collection<? extends GrantedAuthority> authorities = loggedUser.getAuthorities();
        if (userIsAdmin(authorities)) {
            return listAll();
        } else if (userIsNormalUser(authorities)) {
            return loggedUser.getReports();
        }
        return Collections.emptySet();
    }

    private boolean userIsNormalUser(Collection<? extends GrantedAuthority> authorities) {
        return authorities.contains(new SimpleGrantedAuthority("ROLE_USER"));
    }

    private boolean userIsAdmin(Collection<? extends GrantedAuthority> authorities) {
        return authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
}




