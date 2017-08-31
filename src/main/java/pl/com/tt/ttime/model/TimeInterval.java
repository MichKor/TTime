package pl.com.tt.ttime.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import javax.persistence.*;
import java.time.LocalTime;

@Entity
@SequenceGenerator(name = "idgen", sequenceName = "public.time_interval_id_seq", allocationSize = 1)
public class TimeInterval extends AbstractEntity {

    @Column(columnDefinition = "TIME")
    @JsonSerialize(using = LocalTimeSerializer.class)
    private LocalTime startTime;

    @Column(columnDefinition = "TIME")
    @JsonSerialize(using = LocalTimeSerializer.class)
    private LocalTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "day_id")
    @JsonBackReference("dayTimeIntervals")
    private Day day;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "day_week_id")
    @JsonBackReference("dayWeekTimeIntervals")
    private DayWeek dayWeek;

    public TimeInterval() {
        super();
    }

    public TimeInterval(LocalTime startTime, LocalTime endTime, Day day) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.day = day;
    }

    public TimeInterval(LocalTime startTime, LocalTime endTime, Day day, DayWeek dayWeek) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.day = day;
        this.dayWeek = dayWeek;
    }

    public LocalTime getStartTime() {
        return startTime;

    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    public DayWeek getDayWeek() {
        return dayWeek;
    }

    public void setDayWeek(DayWeek dayWeek) {
        this.dayWeek = dayWeek;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeInterval)) return false;
        if (!super.equals(o)) return false;

        TimeInterval that = (TimeInterval) o;

        if (startTime != null ? !startTime.equals(that.startTime) : that.startTime != null) return false;
        return endTime != null ? endTime.equals(that.endTime) : that.endTime == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
        return result;
    }
}
