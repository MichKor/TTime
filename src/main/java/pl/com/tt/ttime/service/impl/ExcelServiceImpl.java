package pl.com.tt.ttime.service.impl;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.com.tt.ttime.rest.model.CSVReportEntry;
import pl.com.tt.ttime.service.ExcelService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ExcelServiceImpl implements ExcelService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelServiceImpl.class);

    @Value("${ttimemanager.jira.url}")
    private String URL;

    private static String END_POINT = "browse/%s";

    private static final Pattern URL_PATTERN = Pattern.compile("[^\\/]+$");

    @Override
    public List<CSVReportEntry> readReport(InputStream stream) throws IOException {
        List<CSVReportEntry> reportEntries = new ArrayList<>();
        CSVReportEntry reportEntry;
        Workbook workbook = new XSSFWorkbook(stream);
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = sheet.iterator();
        iterator.next();
        while (iterator.hasNext()) {
            Row row = iterator.next();
            if (!iterator.hasNext()) {
                break;
            }
            reportEntry = new CSVReportEntry();
            reportEntry.setType(getRowStringValue(row, 1));
            reportEntry.setKey(getRowStringValue(row, 2));
            reportEntry.setTitle(getRowStringValue(row, 3));
            reportEntry.setEpicLink(getRowHyperlinkAddress(row, 4));
            reportEntry.setEpicName(getRowStringValue(row, 4));
            reportEntry.setProject(getRowStringValue(row, 5));
            reportEntry.setReporter(getRowStringValue(row, 6));
            reportEntry.setTeam(getRowStringValue(row, 8));
            reportEntry.setDateStart(getRowStringValue(row, 9));
            reportEntry.setAuthor(getRowStringValue(row, 10));
            reportEntry.setTimeSpent(getRowDoubleValue(row, 11));
            reportEntry.setComment(getRowStringValue(row, 12));
            reportEntry.setPhase(getRowStringValue(row, 13));
            reportEntries.add(reportEntry);
        }
        return reportEntries;
    }

    private static double getRowDoubleValue(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell == null) {
            return 0.0;
        }
        return cell.getNumericCellValue();
    }

    private static String getRowStringValue(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell == null) {
            return null;
        }
        return cell.getStringCellValue();
    }

    private static String getRowHyperlinkAddress(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell == null || cell.getHyperlink() == null || cell.getHyperlink().getAddress() == null) {
            return null;
        }
        Matcher matcher = URL_PATTERN.matcher(cell.getHyperlink().getAddress());
        if (matcher.find()) {
            return matcher.group(0);
        }
        return null;
    }

    @Override
    public byte[] reportToBytes(List<CSVReportEntry> reportEntries) throws IOException {
        return createWorkBook(reportEntries).toByteArray();
    }

    private static final String[] head = {
            "Project", "Type", "Key", "Title", "Epic Link", "Project", "Reporter", "Summary", "Team", "Date", "Username",
            "Time Spent (h)", "Comment", "TS Phase"
    };
    private static final String[] footer = {"", "", "", "", "", "", "", "", "", "", "", "=SUMY.CZĘŚCIOWE(9;L2:Lrownum-1)", "", ""};

    private ByteArrayOutputStream createWorkBook(Collection<CSVReportEntry> entries) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Report");
        int rowNum = 0;
        fillHeader(sheet.createRow(rowNum++));
        for (CSVReportEntry entry : entries) {
            Row row = sheet.createRow(rowNum++);
            int colNum = 0;

            Cell cell = addCell(row, entry.getProject(),colNum++);
            addHyperlink(cell, entry.getProject(), workbook);

            addCell(row, entry.getType(),colNum++);

            cell = addCell(row, entry.getKey(),colNum++);
            addHyperlink(cell, entry.getKey(), workbook);

            addCell(row, entry.getTitle(),colNum++);

            cell = addCell(row, entry.getEpicName(),colNum++);
            addHyperlink(cell, entry.getEpicLink(), workbook);

            addCell(row, entry.getProject(),colNum++);
            addCell(row, entry.getReporter(),colNum++);
            addCell(row, entry.getTitle(),colNum++);
            addCell(row, (entry.getTeam() == null)? "brak" : entry.getTeam(),colNum++);
            addCell(row, entry.getDateStart(),colNum++);
            addCell(row, entry.getAuthor(),colNum++);
            cell = row.createCell(colNum++);
            cell.setCellValue(entry.getTimeSpent());
            addCell(row, entry.getComment(),colNum++);
            addCell(row, entry.getPhase(),colNum);
        }
        fillFooter(sheet.createRow(rowNum));

        ByteArrayOutputStream fileXLS = new ByteArrayOutputStream();
        workbook.write(fileXLS);
        workbook.close();
        return fileXLS;
    }

    private void fillHeader(Row row) {
        int colNum = 0;
        for (String field : head) {
            Cell cell = addCell(row, field, colNum++);
            //TODO: Dodać sortowanie
        }
    }

    private Cell addCell(Row row, String value, int colNum) {
        Cell cell = row.createCell(colNum);
        cell.setCellValue(value);
        return cell;
    }

    private void addHyperlink(Cell cell, String uri, XSSFWorkbook workbook) {
        if (uri == null || uri.equals("")) return;
        CreationHelper createHelper = workbook
                .getCreationHelper();
        XSSFHyperlink link = (XSSFHyperlink) createHelper
                .createHyperlink(Hyperlink.LINK_URL);
        link.setAddress(String.format(URL + END_POINT, uri));
        cell.setHyperlink(link);
        cell.setCellStyle(createHyperlinkStyle(workbook));
    }

    private XSSFCellStyle createHyperlinkStyle(XSSFWorkbook workbook){
        XSSFFont hlinkfont = workbook.createFont();
        hlinkfont.setUnderline(XSSFFont.U_SINGLE);
        hlinkfont.setColor(HSSFColor.BLUE.index);
        XSSFCellStyle hlinkstyle = workbook.createCellStyle();
        hlinkstyle.setFont(hlinkfont);
        return hlinkstyle;
    }

    private void fillFooter(Row row) {
        String strFormula = "SUBTOTAL(9,L2:L" + (row.getRowNum()) + ")";
        int colNum = 0;
        for (String field : footer) {
            Cell cell = row.createCell(colNum++);
            if (row.getLastCellNum() == 12) {
                cell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
                cell.setCellFormula(strFormula);
            } else {
                cell.setCellValue(field);
            }

        }
    }
}
