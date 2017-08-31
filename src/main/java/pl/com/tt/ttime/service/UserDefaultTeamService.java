package pl.com.tt.ttime.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import pl.com.tt.ttime.exception.UserNotAuthenticatedException;
import pl.com.tt.ttime.model.Team;
import pl.com.tt.ttime.model.User;

import java.util.Set;

public interface UserDefaultTeamService extends AbstractService<User, Long>, UserDetailsService {

    User setUserDefaultTeam(Team team) throws UserNotAuthenticatedException;

    User deleteDefaulTeam(Team team) throws UserNotAuthenticatedException;

    void deleteDefaulTeam(Team team, Set<Long> userId) throws UserNotAuthenticatedException;

    Team getUserDefaultTeam() throws UserNotAuthenticatedException;
}
