package pl.com.tt.ttime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.com.tt.ttime.model.Day;
import pl.com.tt.ttime.service.DayService;

import java.util.List;

@RestController
@RequestMapping("/day")
public class DayController extends AbstractController<Day, DayService> {

    @Autowired
    public DayController(DayService service) {
        super(service);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PostMapping("/multiply")
    public ResponseEntity<List<Day>> createMultiDays(@RequestBody List<Day> days) {
        List<Day> resultDays = service.saveAll(days);
        return new ResponseEntity<>(resultDays, HttpStatus.OK);
    }
}
