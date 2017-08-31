package pl.com.tt.ttime.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import pl.com.tt.ttime.exception.RestRequestException;
import pl.com.tt.ttime.exception.UserNotAuthenticatedException;
import pl.com.tt.ttime.model.*;
import pl.com.tt.ttime.rest.JiraRestService;
import pl.com.tt.ttime.security.SecurityUtils;
import pl.com.tt.ttime.service.*;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private UserService userService;
    private TemplateService templateService;
    private TimeIntervalService timeIntervalService;
    private DayService dayService;
    private UserLdapService userLdapService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setTemplateService(TemplateService templateService) {
        this.templateService = templateService;
    }

    @Autowired
    public void setTimeIntervalService(TimeIntervalService timeIntervalService) {
        this.timeIntervalService = timeIntervalService;
    }

    @Autowired
    public void setDayService(DayService dayService) {
        this.dayService = dayService;
    }

    @Autowired
    public void setUserLdapService(UserLdapService userLdapService) {
        this.userLdapService = userLdapService;
    }

    @GetMapping
    public ResponseEntity<User> user(Principal user) {
        if (user == null) {
            return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<User>(SecurityUtils.getCurrentUser(userService), HttpStatus.OK);
    }

    @Secured({"ROLE_ADMIN"})
    @GetMapping(value = "/users")
    public @ResponseBody
    ResponseEntity<List<User>> listAll() {
        List<User> entities = userService.listAll();

        if (entities.isEmpty()) {
            return new ResponseEntity<List<User>>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<List<User>>(entities, HttpStatus.OK);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PutMapping("/{id}")
    public @ResponseBody
    ResponseEntity<User> update(@PathVariable Long id, @RequestBody User obj) {
        User entity = userService.findById(id);

        if (entity == null) {
            return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
        }

        obj.setId(id);
        obj = userService.save(obj);

        return new ResponseEntity<User>(obj, HttpStatus.OK);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping(value = "/teams")
    public ResponseEntity<Map<String, Object>> getGroups() {
        List<Team> usersTeams = null;
        List<Team> observedTeams = null;

        try {
            usersTeams = userService.getUsersTeams();
            observedTeams = userService.getUsersObservedTeams();
        } catch (UserNotAuthenticatedException e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<Map<String, Object>>(HttpStatus.FORBIDDEN);
        }

        Set<User> users = new HashSet<>();
        for (Team t : usersTeams) {
            users.addAll(t.getUsers().stream().map(UserTeam::getUser).collect(Collectors.toList()));
        }
        for (Team t : observedTeams) {
            users.addAll(t.getUsers().stream().map(UserTeam::getUser).collect(Collectors.toList()));
        }
        Map<Long, List<TimeInterval>> timeIntervals = timeIntervalService.getIntervalsForUsers(users, LocalDate.now());

        Map<String, Object> map = new HashMap<>();
        map.put("observedTeams", observedTeams);
        map.put("myTeams", usersTeams);
        map.put("schedules", timeIntervals);

        return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping(value = "/templates")
    public ResponseEntity<Map<String, Object>> getTemplates() {
        Set<Template> templates = null;
        Template defaultTemplate = null;

        try {
            templates = userService.getUsersTemplates();
            defaultTemplate = userService.getUsersDefaultTemplate();
        } catch (UserNotAuthenticatedException e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<Map<String, Object>>(HttpStatus.FORBIDDEN);
        }

        Map<String, Object> retMap = new HashMap<>();
        retMap.put("defaultId", (defaultTemplate != null) ? defaultTemplate.getId() : null);
        retMap.put("templates", templates);

        return new ResponseEntity<Map<String, Object>>(retMap, HttpStatus.OK);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PutMapping(value = "/default-template")
    public ResponseEntity<Void> setDefaultTemplate(@PathParam("temp-id") long tempId) {
        Template template = templateService.findById(tempId);

        if (template == null) {
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        try {
            userService.setUsersDefaultTemplate(template);
        } catch (UserNotAuthenticatedException e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<Void>(HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping(value = "/days")
    public ResponseEntity<Set<Day>> getDays() {
        try {
            return new ResponseEntity<Set<Day>>(userService.getUsersSchedule(), HttpStatus.OK);
        } catch (UserNotAuthenticatedException e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<Set<Day>>(HttpStatus.FORBIDDEN);
        }
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping(value = "/days/userId")
    public ResponseEntity<Set<Day>> getDaysByUserId(@PathParam("userId") long userId) {
        Set<Day> days = userService.getOtherUsersScheduleByUserId(userId);

        if (days == null || days.isEmpty()) {
            return new ResponseEntity<Set<Day>>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<Set<Day>>(days, HttpStatus.OK);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping(value = "/days/userId/between")
    public ResponseEntity<Set<Day>> getDaysByUserIdBetweenDates(@RequestParam("userId") long userId, @RequestParam("start") String start, @RequestParam("end") String end) {
        User userOfSchedule = userService.findById(userId);

        if (userOfSchedule == null) {
            return new ResponseEntity<Set<Day>>(HttpStatus.FORBIDDEN);
        }

        Set<Day> days = dayService.findDaysBetween(userOfSchedule, LocalDate.parse(start), LocalDate.parse(end));

        if (days.isEmpty()) {
            return new ResponseEntity<Set<Day>>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<Set<Day>>(days, HttpStatus.OK);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PutMapping(value = "/details")
    public ResponseEntity<User> updateUsersDetails(@RequestBody User user) {
        try {
            return new ResponseEntity<User>(userService.updateUsersDetails(user), HttpStatus.OK);
        } catch (UserNotAuthenticatedException e) {
            return new ResponseEntity<User>(HttpStatus.FORBIDDEN);
        }
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping(value = "/days/userId/date")
    public ResponseEntity<Day> getDayByUserIdAndDate(@RequestParam("userId") long userId, @RequestParam("date") String date) {
        User userOfSchedule = userService.findById(userId);

        if (userOfSchedule == null) {
            return new ResponseEntity<Day>(HttpStatus.FORBIDDEN);
        }

        Day day = dayService.findDay(userOfSchedule, LocalDate.parse(date));

        if (day == null) {
            return new ResponseEntity<Day>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<Day>(day, HttpStatus.OK);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping(value = "/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam(value = "query", defaultValue = "") String query) {
        return new ResponseEntity<List<User>>(userLdapService.queryUsers(query), HttpStatus.OK);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PutMapping(value = "/loggedInBefore")
    public ResponseEntity<User> setLoggedInBefore() {
        try {
            return new ResponseEntity<User>(userService.setLoggedInBefore(), HttpStatus.OK);
        } catch (UserNotAuthenticatedException e) {
            return new ResponseEntity<User>(HttpStatus.FORBIDDEN);
        }
    }
}
