package pl.com.tt.ttime.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.com.tt.ttime.exception.UserDoesntHaveDefTemplateException;
import pl.com.tt.ttime.exception.UserNotAuthenticatedException;
import pl.com.tt.ttime.exception.UserNotFoundException;
import pl.com.tt.ttime.model.*;
import pl.com.tt.ttime.repository.UserRepository;
import pl.com.tt.ttime.security.SecurityUtils;
import pl.com.tt.ttime.service.*;
import pl.com.tt.ttime.util.HolidayUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserServiceImpl extends AbstractServiceImpl<User, Long, UserRepository>
        implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private DayService dayService;
    private TemplateService templateService;
    private RoleService roleService;
    private UserLdapService userLdapService;

    @Autowired
    public UserServiceImpl(UserRepository repository) {
        super(repository);
    }

    @Autowired
    public void setDayService(DayService dayService) {
        this.dayService = dayService;
    }

    @Autowired
    public void setTemplateService(TemplateService templateService) {
        this.templateService = templateService;
    }

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    @Autowired
    public void setUserLdapService(UserLdapService userLdapService) {
        this.userLdapService = userLdapService;
    }

    @Override
    public User findByUsername(String username) {
        return repository.findByUsername(username);
    }

    @Override
    @Transactional
    public User findOrCreateNewUser(String username) throws UserNotFoundException {
        User existingUser = findByUsername(username);
        if (existingUser == null) {
            existingUser = userLdapService.getUser(username);
            if (existingUser != null) {
                return registerNewUser(existingUser);
            }
            throw new UserNotFoundException("User " + username + " has not been found in database nor LDAP.");
        }
        return existingUser;
    }

    @Override
    @Transactional
    public User findOrCreateNewUser(User user) {
        User existingUser = findByUsername(user.getUsername());
        if (existingUser == null) {
            return registerNewUser(user);
        }
        return existingUser;
    }

    @Override
    @Transactional
    public User registerNewUser(User user) {
        user.getRoles().add(roleService.findByName("ROLE_USER"));
        return save(user);
    }

    @Override
    public Set<User> findUsersWithDefaultTemplate() {
        return repository.findUsersWithDefaultTemplate();
    }

    @Transactional(readOnly = true)
    private User getCurrentUser() throws UserNotAuthenticatedException {
        User currentUser = SecurityUtils.getCurrentUser(this);

        if (currentUser == null) {
            throw new UserNotAuthenticatedException("User is not authenticated and tried to access protected resource!");
        }

        return currentUser;
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Template> getUsersTemplates() throws UserNotAuthenticatedException {
        return getCurrentUser().getTemplates();
    }

    @Override
    @Transactional(readOnly = true)
    public Template getUsersDefaultTemplate() throws UserNotAuthenticatedException {
        return getCurrentUser().getDefaultTemplate();
    }

    @Override
    public List<Team> getUsersTeams() throws UserNotAuthenticatedException {
        return repository.getUsersTeams(getCurrentUser());
    }

    @Override
    public List<Team> getUsersObservedTeams() throws UserNotAuthenticatedException {
        return repository.getUsersObservedTeams(getCurrentUser());
    }

    @Override
    public Set<Day> getUsersSchedule() throws UserNotAuthenticatedException {
        return getCurrentUser().getDays();
    }

    @Override
    @Transactional
    public Set<Day> getOtherUsersScheduleByUserId(long userId) {
        User userOfSchedule = findById(userId);

        if (userOfSchedule == null) {
            return null;
        }

        return userOfSchedule.getDays();
    }

    @Override
    @Transactional
    public User setUsersDefaultTemplate(Template template) throws UserNotAuthenticatedException {
        User currentUser = getCurrentUser();
        currentUser.setDefaultTemplate(template);
        return save(currentUser);
    }

    private LocalDate getFirstWeekDay(LocalDate date) {
        while (date.getDayOfWeek() != DayOfWeek.MONDAY) {
            date = date.minusDays(1);
        }
        return date;
    }

    @Override
    @Transactional
    public boolean assignDefTemplateUnlessSet(User user, LocalDate date) throws UserDoesntHaveDefTemplateException {
        LocalDate dayOfWeek = getFirstWeekDay(date);
        LocalDate endOfWeek = dayOfWeek.plusWeeks(1).minusDays(2);

        if (user.getDefaultTemplate() == null) {
            throw new UserDoesntHaveDefTemplateException("Tried to assign a default template for a user who doesn't have a default template!");
        }

        Set<Day> oldDays = dayService.findDaysBetween(user, dayOfWeek, endOfWeek);
        if (oldDays.isEmpty()) {
            assignTemplateForWeek(user, user.getDefaultTemplate(), dayOfWeek);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public void assignTemplateForWeek(User user, Template template, LocalDate date) {
        LocalDate dayOfWeek = getFirstWeekDay(date);
        LocalDate endOfWeek = dayOfWeek.plusWeeks(1).minusDays(2);
        List<LocalDate> holiDays;
        if (dayOfWeek.getYear() != endOfWeek.getYear()) {
            holiDays = Stream.concat(HolidayUtils.getHolidaysForYear(dayOfWeek.getYear()).stream(), HolidayUtils.getHolidaysForYear(endOfWeek.getYear()).stream()).sorted().collect(Collectors.toList());
        }
        else {
            holiDays = HolidayUtils.getHolidaysForYear(dayOfWeek.getYear());
        }

        user = findById(user.getId());
        template = templateService.findById(template.getId());

        Set<Day> oldDays = dayService.findDaysBetween(user, dayOfWeek, endOfWeek);
        if (!oldDays.isEmpty()) {
            user.getDays().removeAll(oldDays);
        }

        Set<Day> days = new HashSet<>();
        if (template.getDayWeeks().isEmpty()) {
            while (dayOfWeek.isBefore(endOfWeek)) {
                if (holiDays.contains(dayOfWeek)) {
                    dayOfWeek = dayOfWeek.plusDays(1);
                    continue;
                }
                days.add(new Day(dayOfWeek, user, new HashSet<>()));
                dayOfWeek = dayOfWeek.plusDays(1);
            }
        } else {
            Map<DayOfWeek, Set<TimeInterval>> templateIntervals = new HashMap<>();
            Set<TimeInterval> timeIntervals;
            Day newDay;
            TimeInterval newInterval;

            for (DayWeek dayWeek : template.getDayWeeks()) {
                templateIntervals.put(dayWeek.getDay(), dayWeek.getTimeIntervals());
            }

            while (dayOfWeek.isBefore(endOfWeek)) {
                if (holiDays.contains(dayOfWeek)) {
                    dayOfWeek = dayOfWeek.plusDays(1);
                    continue;
                }
                if (templateIntervals.containsKey(dayOfWeek.getDayOfWeek())) {
                    newDay = new Day(dayOfWeek, user);
                    timeIntervals = new HashSet<>();
                    for (TimeInterval timeInterval : templateIntervals.get(dayOfWeek.getDayOfWeek())) {
                        newInterval = new TimeInterval(timeInterval.getStartTime(), timeInterval.getEndTime(), newDay);
                        timeIntervals.add(newInterval);
                    }
                    newDay.setTimeIntervals(timeIntervals);
                    days.add(newDay);
                } else {
                    days.add(new Day(dayOfWeek, user, new HashSet<>()));
                }
                dayOfWeek = dayOfWeek.plusDays(1);
            }
        }

        user.getDays().addAll(days);
        save(user);
    }

    @Override
    public User updateUsersDetails(User user) throws UserNotAuthenticatedException {
        User currentUser = getCurrentUser();
        if (!currentUser.getId().equals(user.getId())) {
            throw new UserNotAuthenticatedException("User tried to modify other user's details!");
        }
        currentUser.setNotificationsEnabled(user.isNotificationsEnabled());
        currentUser.setLocaleName(user.getLocaleName());
        currentUser = save(currentUser);
        return currentUser;
    }

    @Override
    public User setLoggedInBefore() throws UserNotAuthenticatedException {
        User currentUser = getCurrentUser();
        currentUser.setLoggedInBefore(true);
        return save(currentUser);
    }

    @Override
    public Integer countUserTeams() {
        User currentUser = SecurityUtils.getCurrentUser();
        return repository.countUserTeams(currentUser);
    }

    @Override
    public Set<User> findAllByUsername(Collection<String> usernames) {
        Set<User> u = new HashSet<>();
        usernames.forEach(username -> {
            try {
                u.add(findOrCreateNewUser(username));
            } catch (UserNotFoundException e) {
                LOGGER.error(e.getMessage(), e);
            }
        });
        return u;
    }
}
