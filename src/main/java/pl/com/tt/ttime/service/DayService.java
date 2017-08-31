package pl.com.tt.ttime.service;

import pl.com.tt.ttime.model.Day;
import pl.com.tt.ttime.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface DayService extends AbstractService<Day, Long> {

    Day findDay(User u, LocalDate date);

    List<Day> saveAll(List<Day> days);

    Set<Day> findDaysBetween(User u, LocalDate startDate, LocalDate endDate);


}
