package pl.com.tt.ttime.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TimesheetReport {
    @JsonDeserialize(using = LongToLocalDateDeserializer.class)
    private LocalDate startDate;

    @JsonDeserialize(using = LongToLocalDateDeserializer.class)
    private LocalDate endDate;

    private List<WorklogTask> worklog;

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<WorklogTask> getWorklog() {
        return worklog;
    }

    public void setWorklog(List<WorklogTask> worklog) {
        this.worklog = worklog;
    }
}
