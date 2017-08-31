package pl.com.tt.ttime.service;

import org.springframework.web.multipart.MultipartFile;
import pl.com.tt.ttime.model.Report;
import pl.com.tt.ttime.rest.model.CSVReportEntry;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

public interface ReportService extends AbstractService<Report, Long> {

    Report saveFile(MultipartFile file) throws IOException, IllegalStateException;

    Collection<Report> getLoggedUserReports();

    byte[] generateFileFromReport(Report reportUser) throws IOException, URISyntaxException;

    byte[] generateNewFileReport(List<CSVReportEntry> reportCSV) throws IOException, URISyntaxException;

    Boolean checkIfJiraReportsEnabled();
}
