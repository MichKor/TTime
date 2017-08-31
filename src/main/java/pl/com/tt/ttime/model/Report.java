package pl.com.tt.ttime.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@SequenceGenerator(name = "idgen", sequenceName = "public.report_id_seq", allocationSize = 1)
@Table(name = "report")
public class Report extends AbstractEntity {

    @Column
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime uploadTimestamp;

    @Column
    @JsonIgnore
    private byte[] reportFile;

    @Column
    private String fileName;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_report", joinColumns = @JoinColumn(name = "report_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    private Set<User> users = new HashSet<>();

    public Report() {
        uploadTimestamp = LocalDateTime.now();
    }

    public boolean addUserToReport(User u){
        return users.add(u);
    }

    public boolean addUsersToReport(Collection<User> users){
        return this.users.addAll(users);
    }

    public LocalDateTime getUploadTimestamp() {
        return uploadTimestamp;
    }

    public void setUploadTimestamp(LocalDateTime uploadTimestamp) {
        this.uploadTimestamp = uploadTimestamp;
    }

    public byte[] getReportFile() {
        return reportFile;
    }

    @JsonIgnore
    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileName(@NotEmpty Collection<String> nameProjects) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        this.fileName = uploadTimestamp.format(formatter);
        nameProjects.forEach(nameProject-> this.fileName+= "_"+nameProject);
        this.fileName+=".xlsx";
    }
    public void setReportFile(byte[] reportFile) {
        this.reportFile = reportFile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Report)) return false;
        if (!super.equals(o)) return false;

        Report report = (Report) o;

        if (uploadTimestamp != null ? !uploadTimestamp.equals(report.uploadTimestamp) : report.uploadTimestamp != null)
            return false;
        if (!Arrays.equals(reportFile, report.reportFile)) return false;
        return fileName != null ? fileName.equals(report.fileName) : report.fileName == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (uploadTimestamp != null ? uploadTimestamp.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(reportFile);
        result = 31 * result + (fileName != null ? fileName.hashCode() : 0);
        return result;
    }
}
