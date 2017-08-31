package pl.com.tt.ttime.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Set;

@Entity(name = "template")
@SequenceGenerator(name = "idgen", sequenceName = "public.template_id_seq", allocationSize = 1)
@Table(name = "template")
public class Template extends AbstractEntity {

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private User user;

    @OneToMany(mappedBy = "template", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("templateDays")
    private Set<DayWeek> dayWeeks;

    public Template() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<DayWeek> getDayWeeks() {
        return dayWeeks;
    }

    public void setDayWeeks(Set<DayWeek> dayWeeks) {
        this.dayWeeks = dayWeeks;
    }

    @Override
    public String toString() {
        return "Template{" +
                "name='" + name + '\'' +
                ", user=" + user +
                '}';
    }
}
