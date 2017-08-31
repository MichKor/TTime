package pl.com.tt.ttime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.com.tt.ttime.model.DayWeek;

public interface DayWeekRepository extends JpaRepository<DayWeek, Long> {
}
