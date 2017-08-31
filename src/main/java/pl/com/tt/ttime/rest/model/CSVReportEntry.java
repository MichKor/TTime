package pl.com.tt.ttime.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.Duration;
import java.time.LocalDateTime;

public class CSVReportEntry {
    private String project;

    private String type;

    private String key;

    private String title;

    private String reporter;

    private String dateStart;

    private double timeSpent;

    private String comment;

    private String author;

    private String authorFullName;

    private String epicLink;

    private String epicName;

    private String phase;

    private String team;

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public double getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(double timeSpent) {
        this.timeSpent = timeSpent;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorFullName() {
        return authorFullName;
    }

    public void setAuthorFullName(String authorFullName) {
        this.authorFullName = authorFullName;
    }

    public String getEpicLink() {
        return epicLink;
    }

    public void setEpicLink(String epicLink) {
        this.epicLink = epicLink;
    }

    public String getEpicName() {
        return epicName;
    }

    public void setEpicName(String epicName) {
        this.epicName = epicName;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CSVReportEntry entry = (CSVReportEntry) o;

        if (Double.compare(entry.timeSpent, timeSpent) != 0) return false;
        if (project != null ? !project.equals(entry.project) : entry.project != null) return false;
        if (type != null ? !type.equals(entry.type) : entry.type != null) return false;
        if (key != null ? !key.equals(entry.key) : entry.key != null) return false;
        if (title != null ? !title.equals(entry.title) : entry.title != null) return false;
        if (reporter != null ? !reporter.equals(entry.reporter) : entry.reporter != null) return false;
        if (dateStart != null ? !dateStart.equals(entry.dateStart) : entry.dateStart != null) return false;
        if (comment != null ? !comment.equals(entry.comment) : entry.comment != null) return false;
        if (author != null ? !author.equals(entry.author) : entry.author != null) return false;
        if (authorFullName != null ? !authorFullName.equals(entry.authorFullName) : entry.authorFullName != null)
            return false;
        if (epicLink != null ? !epicLink.equals(entry.epicLink) : entry.epicLink != null) return false;
        if (epicName != null ? !epicName.equals(entry.epicName) : entry.epicName != null) return false;
        if (phase != null ? !phase.equals(entry.phase) : entry.phase != null) return false;
        return team != null ? team.equals(entry.team) : entry.team == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = project != null ? project.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (key != null ? key.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (reporter != null ? reporter.hashCode() : 0);
        result = 31 * result + (dateStart != null ? dateStart.hashCode() : 0);
        temp = Double.doubleToLongBits(timeSpent);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (authorFullName != null ? authorFullName.hashCode() : 0);
        result = 31 * result + (epicLink != null ? epicLink.hashCode() : 0);
        result = 31 * result + (epicName != null ? epicName.hashCode() : 0);
        result = 31 * result + (phase != null ? phase.hashCode() : 0);
        result = 31 * result + (team != null ? team.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CSVReportEntry{" +
                "project='" + project + '\'' +
                ", type='" + type + '\'' +
                ", key='" + key + '\'' +
                ", title='" + title + '\'' +
                ", reporter='" + reporter + '\'' +
                ", dateStart='" + dateStart + '\'' +
                ", timeSpent=" + timeSpent +
                ", comment='" + comment + '\'' +
                ", author='" + author + '\'' +
                ", authorFullName='" + authorFullName + '\'' +
                ", epicLink='" + epicLink + '\'' +
                ", epicName='" + epicName + '\'' +
                ", phase='" + phase + '\'' +
                ", team='" + team + '\'' +
                '}';
    }
}
