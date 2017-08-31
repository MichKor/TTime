package pl.com.tt.ttime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.com.tt.ttime.model.Day;
import pl.com.tt.ttime.model.User;

import java.time.LocalDate;
import java.util.Set;

public interface DayRepository extends JpaRepository<Day, Long> {

    Day findByUserAndDate(User u, LocalDate date);

    @Query("select d from Day d where d.user = :u and d.date >= :start and d.date < :end")
    Set<Day> findDaysBetween(@Param("u") User u, @Param("start") LocalDate startDate, @Param("end") LocalDate endDate);
}
