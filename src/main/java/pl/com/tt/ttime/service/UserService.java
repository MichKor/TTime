package pl.com.tt.ttime.service;

import pl.com.tt.ttime.exception.UserDoesntHaveDefTemplateException;
import pl.com.tt.ttime.exception.UserNotAuthenticatedException;
import pl.com.tt.ttime.exception.UserNotFoundException;
import pl.com.tt.ttime.model.Day;
import pl.com.tt.ttime.model.Team;
import pl.com.tt.ttime.model.Template;
import pl.com.tt.ttime.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface UserService extends AbstractService<User, Long> {
    User findByUsername(String username);

    User findOrCreateNewUser(String username) throws UserNotFoundException;
    User findOrCreateNewUser(User user);

    User registerNewUser(User user);

    User updateUsersDetails(User user) throws UserNotAuthenticatedException;

    User setLoggedInBefore() throws UserNotAuthenticatedException;

    Set<User> findUsersWithDefaultTemplate();

    User setUsersDefaultTemplate(Template template) throws UserNotAuthenticatedException;

    Template getUsersDefaultTemplate() throws UserNotAuthenticatedException;

    Set<Template> getUsersTemplates() throws UserNotAuthenticatedException;

    List<Team> getUsersTeams() throws UserNotAuthenticatedException;

    List<Team> getUsersObservedTeams() throws UserNotAuthenticatedException;

    Set<Day> getUsersSchedule() throws UserNotAuthenticatedException;

    boolean assignDefTemplateUnlessSet(User user, LocalDate date) throws UserDoesntHaveDefTemplateException;

    void assignTemplateForWeek(User user, Template template, LocalDate dayOfWeek);

    Set<Day> getOtherUsersScheduleByUserId(long userId);

    Integer countUserTeams();

    Set<User> findAllByUsername(Collection<String> usernames);
}
