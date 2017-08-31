package pl.com.tt.ttime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.com.tt.ttime.model.TimeInterval;
import pl.com.tt.ttime.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TimeIntervalRepository extends JpaRepository<TimeInterval, Long> {
    @Query("SELECT new map(u.id as id, ti as interval) " +
            "FROM User u " +
            "JOIN u.days day " +
            "JOIN day.timeIntervals ti " +
            "WHERE u in (:users) AND day.date = (:day) ")
    List<Map<String, Object>> getIntervalsForUsers(@Param("users") Set<User> users, @Param("day") LocalDate date);
}
