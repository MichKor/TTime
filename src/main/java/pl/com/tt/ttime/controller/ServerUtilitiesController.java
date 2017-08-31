package pl.com.tt.ttime.controller;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.com.tt.ttime.util.HolidayUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ServerUtilitiesController {

    @GetMapping("/time")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    public LocalDateTime getServerLocalDateTime() {
        return LocalDateTime.now();
    }

    @GetMapping("/holidays")
    @JsonSerialize(using = LocalDateSerializer.class)
    public List<LocalDate> getPolishHolidays(@RequestParam(value = "year", defaultValue = "0") int year) {
        if (year == 0) {
            year = LocalDate.now().getYear();
        }
        return HolidayUtils.getHolidaysForYear(year);
    }
}
