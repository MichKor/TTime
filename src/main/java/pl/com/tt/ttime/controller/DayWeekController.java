package pl.com.tt.ttime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.com.tt.ttime.model.DayWeek;
import pl.com.tt.ttime.service.DayWeekService;

@RestController
@RequestMapping("/day-week")
public class DayWeekController extends AbstractController<DayWeek, DayWeekService> {

    @Autowired
    public DayWeekController(DayWeekService service) {
        super(service);
    }
}
