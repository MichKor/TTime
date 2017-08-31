package pl.com.tt.ttime.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.com.tt.ttime.model.Day;
import pl.com.tt.ttime.model.User;
import pl.com.tt.ttime.repository.DayRepository;
import pl.com.tt.ttime.security.SecurityUtils;
import pl.com.tt.ttime.service.DayService;
import pl.com.tt.ttime.service.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class DayServiceImpl extends AbstractServiceImpl<Day, Long, DayRepository> implements DayService {

    @Autowired
    public DayServiceImpl(DayRepository repository) {
        super(repository);
    }

    @Override
    public Day findDay(User u, LocalDate date) {
        return repository.findByUserAndDate(u, date);
    }

    @Override
    public List<Day> saveAll(List<Day> days) {
        User u = SecurityUtils.getCurrentUser();
        days.removeIf(day -> LocalDate.now().isAfter(day.getDate()));
        days.forEach(day -> day.setUser(u));
        deleteExistDays(days);
        return super.saveAll(days);
    }

    private void deleteExistDays(List<Day> days) {
        List<Day> existDays = new ArrayList<>();
        days.forEach(day ->
                existDays.add(findDay(day.getUser(), day.getDate()))
        );
        existDays.forEach(d -> {
            if (d != null) delete(d);
        });
    }

    @Override
    public Set<Day> findDaysBetween(User u, LocalDate startDate, LocalDate endDate) {
        return repository.findDaysBetween(u, startDate, endDate);
    }

}
