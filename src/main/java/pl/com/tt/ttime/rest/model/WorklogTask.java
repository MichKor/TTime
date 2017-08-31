package pl.com.tt.ttime.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WorklogTask {
    private String key;
    private String summary;
    private List<WorklogEntry> entries;
    private List<WorklogField> fields;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<WorklogEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<WorklogEntry> entries) {
        this.entries = entries;
    }

    public List<WorklogField> getFields() {
        return fields;
    }

    public void setFields(List<WorklogField> fields) {
        this.fields = fields;
    }
}
