package pl.com.tt.ttime.service;

import pl.com.tt.ttime.model.TimeInterval;
import pl.com.tt.ttime.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TimeIntervalService extends AbstractService<TimeInterval, Long> {
    Map<Long, List<TimeInterval>> getIntervalsForUsers(Set<User> users, LocalDate date);
}
