package pl.com.tt.ttime.rest.model;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimesheetReportToCSVMapper {

    private static final Pattern EPIC_LINK_PATTERN = Pattern.compile("href=\"([0-9A-Za-z\\-_/]+/([0-9A-Za-z\\-_]+))\"");
    private static final Pattern EPIC_NAME_PATTERN = Pattern.compile("<a.+>(\\s*(\\S.+\\S)\\s*)<\\/a>");

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public static List<CSVReportEntry> mapToCsvReport(TimesheetReport timesheetReport) {
        List<CSVReportEntry> reportEntries = new ArrayList<>();
        CSVReportEntry report;
        for (WorklogTask task : timesheetReport.getWorklog()) {
            for (WorklogEntry entry : task.getEntries()) {
                report = new CSVReportEntry();
                report.setKey(task.getKey());
                report.setTitle(task.getSummary());
                report.setTimeSpent(entry.getTimeSpent().getSeconds() / 3600.0);
                report.setDateStart(dateTimeFormatter.format(entry.getStartDate()));
                report.setComment(entry.getComment());
                report.setAuthor(entry.getAuthor());
                report.setAuthorFullName(entry.getAuthorFullName());
                for (WorklogField field : task.getFields()) {
                    if (field.getLabel().equals("NoValueForFieldOnIssue") || field.getLabel().equals("N/A")) {
                        continue;
                    }

                    switch (field.getValue()) {
                        case "project":
                            report.setProject(field.getLabel());
                            break;
                        case "reporter":
                            report.setReporter(field.getLabel());
                            break;
                        case "issuetype":
                            report.setType(field.getLabel());
                            break;
                        case "customfield_11800":
                            String f = field.getLabel().replaceAll("\r\n", "");
                            Matcher matcher = EPIC_LINK_PATTERN.matcher(f);
                            if (matcher.find()) {
                                report.setEpicLink(matcher.group(2));
                            }
                            matcher = EPIC_NAME_PATTERN.matcher(f);
                            if (matcher.find()) {
                                report.setEpicName(matcher.group(2));
                            }
                            break;
                        case "team":
                            report.setTeam(field.getLabel());
                            break;
                    }
                }
                reportEntries.add(report);
            }
        }
        return reportEntries;
    }
}
