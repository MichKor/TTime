package pl.com.tt.ttime.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@SequenceGenerator(name = "idgen", sequenceName = "public.day_id_seq", allocationSize = 1)
public class Day extends AbstractEntity {

    @Column
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "day", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference("dayTimeIntervals")
    private Set<TimeInterval> timeIntervals;

    public Day() {
        super();
    }

    public Day(LocalDate date, User user) {
        this.date = date;
        this.user = user;
    }

    public Day(LocalDate date, User user, Set<TimeInterval> timeIntervals) {
        this.date = date;
        this.user = user;
        this.timeIntervals = timeIntervals;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<TimeInterval> getTimeIntervals() {
        return timeIntervals;
    }

    public void setTimeIntervals(Set<TimeInterval> timeIntervals) {
        this.timeIntervals = timeIntervals;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Day)) return false;
        if (!super.equals(o)) return false;

        Day day = (Day) o;

        return date != null ? date.equals(day.date) : day.date == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }
}
