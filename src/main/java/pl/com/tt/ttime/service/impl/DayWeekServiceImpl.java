package pl.com.tt.ttime.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.com.tt.ttime.model.DayWeek;
import pl.com.tt.ttime.repository.DayWeekRepository;
import pl.com.tt.ttime.service.DayWeekService;

@Service
public class DayWeekServiceImpl extends AbstractServiceImpl<DayWeek, Long, DayWeekRepository> implements DayWeekService {

    @Autowired
    public DayWeekServiceImpl(DayWeekRepository repository) {
        super(repository);
    }
}
