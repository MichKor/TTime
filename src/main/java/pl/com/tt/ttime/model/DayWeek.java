package pl.com.tt.ttime.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.util.Set;

@Entity
@SequenceGenerator(name = "idgen", sequenceName = "public.day_week_id_seq", allocationSize = 1)
public class DayWeek extends AbstractEntity {

    @Enumerated(EnumType.STRING)
    private DayOfWeek day;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference("templateDays")
    private Template template;

    @OneToMany(mappedBy = "dayWeek", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("dayWeekTimeIntervals")
    private Set<TimeInterval> timeIntervals;

    public DayWeek() {
        super();
    }

    public DayWeek(DayOfWeek day, Template template, Set<TimeInterval> timeIntervals) {
        this.day = day;
        this.template = template;
        this.timeIntervals = timeIntervals;
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    public Set<TimeInterval> getTimeIntervals() {
        return timeIntervals;
    }

    public void setTimeIntervals(Set<TimeInterval> timeIntervals) {
        this.timeIntervals = timeIntervals;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public void setDay(DayOfWeek day) {
        this.day = day;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DayWeek)) return false;
        if (!super.equals(o)) return false;

        DayWeek dayWeek = (DayWeek) o;

        return day != null ? day.equals(dayWeek.day) : dayWeek.day == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (day != null ? day.hashCode() : 0);
        return result;
    }
}
