package pl.com.tt.ttime.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.com.tt.ttime.exception.UserNotAuthenticatedException;
import pl.com.tt.ttime.model.Team;
import pl.com.tt.ttime.model.User;
import pl.com.tt.ttime.model.UserTeam;
import pl.com.tt.ttime.repository.UserRepository;
import pl.com.tt.ttime.security.SecurityUtils;
import pl.com.tt.ttime.service.UserDefaultTeamService;
import pl.com.tt.ttime.service.UserService;

import javax.transaction.Transactional;
import java.util.Set;

@Service
public class UserDefaultTeamServiceImpl extends AbstractServiceImpl<User, Long, UserRepository> implements UserDefaultTeamService {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public UserDefaultTeamServiceImpl(UserRepository repository) {
        super(repository);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return user;
    }

    @Override
    public User setUserDefaultTeam(Team team) throws UserNotAuthenticatedException {
        User currentUser = SecurityUtils.getCurrentUser(userService);
        Set<UserTeam> usersInTeam = team.getUsers();

        for (UserTeam user : usersInTeam) {
            if (user.getUser().getId() == currentUser.getId()) {
                currentUser.setDefaultTeam(team);
            }
        }

        return save(currentUser);
    }

    @Override
    public User deleteDefaulTeam(Team team) throws UserNotAuthenticatedException {
        User currentUser = SecurityUtils.getCurrentUser(userService);
        Set<UserTeam> usersInTeam = team.getUsers();

        for (UserTeam user : usersInTeam) {
            if (user.getUser().getDefaultTeam() != null && user.getUser().getDefaultTeam().getId().equals(team.getId())) {
                currentUser.setDefaultTeam(null);
            }
        }

        return save(currentUser);
    }

    @Override
    public void deleteDefaulTeam(Team team, Set<Long> userId) throws UserNotAuthenticatedException {
        for (Long id : userId) {
            User user = findById(id);
            if (user.getDefaultTeam() != null && user.getDefaultTeam().getId().equals(team.getId())) {
                user.setDefaultTeam(null);
                save(user);
            }
        }
    }

    @Override
    @Transactional
    public Team getUserDefaultTeam() throws UserNotAuthenticatedException {
        User currentUser = SecurityUtils.getCurrentUser(userService);
        return currentUser.getDefaultTeam();
    }
}
