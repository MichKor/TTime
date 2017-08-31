package pl.com.tt.ttime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.com.tt.ttime.model.TimeInterval;
import pl.com.tt.ttime.service.TimeIntervalService;

@RestController
@RequestMapping("/time-interval")
public class TimeIntervalController extends AbstractController<TimeInterval, TimeIntervalService> {

    @Autowired
    public TimeIntervalController(TimeIntervalService service) {
        super(service);
    }
}
