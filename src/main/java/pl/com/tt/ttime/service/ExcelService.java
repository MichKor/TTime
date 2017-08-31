package pl.com.tt.ttime.service;

import pl.com.tt.ttime.rest.model.CSVReportEntry;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;

public interface ExcelService {
    List<CSVReportEntry> readReport(InputStream stream) throws IOException;
    byte[] reportToBytes(List<CSVReportEntry> reportEntries) throws IOException, URISyntaxException;
}
