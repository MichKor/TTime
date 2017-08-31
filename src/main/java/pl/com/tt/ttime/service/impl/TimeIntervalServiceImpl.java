package pl.com.tt.ttime.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.com.tt.ttime.model.TimeInterval;
import pl.com.tt.ttime.model.User;
import pl.com.tt.ttime.repository.TimeIntervalRepository;
import pl.com.tt.ttime.service.TimeIntervalService;

import java.time.LocalDate;
import java.util.*;

@Service
public class TimeIntervalServiceImpl extends AbstractServiceImpl<TimeInterval, Long, TimeIntervalRepository>
        implements TimeIntervalService {

    @Autowired
    public TimeIntervalServiceImpl(TimeIntervalRepository repository) {
        super(repository);
    }

    @Override
    public Map<Long, List<TimeInterval>> getIntervalsForUsers(Set<User> users, LocalDate date) {
        if (users.isEmpty()) {
            return null;
        }

        List<Map<String, Object>> intervals = repository.getIntervalsForUsers(users, date);
        Map<Long, List<TimeInterval>> returnValue = new HashMap<>();
        TimeInterval interval;
        long userId;
        for (Map<String, Object> map : intervals) {
            userId = (long) map.get("id");
            interval = (TimeInterval) map.get("interval");
            if (returnValue.containsKey(userId)) {
                returnValue.get(userId).add(interval);
            } else {
                List<TimeInterval> list = new ArrayList<>();
                list.add(interval);
                returnValue.put(userId, list);
            }
        }
        return returnValue;
    }
}
