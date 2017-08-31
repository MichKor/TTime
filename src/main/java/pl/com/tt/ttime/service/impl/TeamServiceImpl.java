package pl.com.tt.ttime.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.com.tt.ttime.exception.*;
import pl.com.tt.ttime.model.Team;
import pl.com.tt.ttime.model.User;
import pl.com.tt.ttime.model.UserTeam;
import pl.com.tt.ttime.repository.TeamRepository;
import pl.com.tt.ttime.security.SecurityUtils;
import pl.com.tt.ttime.service.TeamService;
import pl.com.tt.ttime.service.UserService;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Service
public class TeamServiceImpl extends AbstractServiceImpl<Team, Long, TeamRepository> implements TeamService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TeamServiceImpl.class);

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public TeamServiceImpl(TeamRepository repository) {
        super(repository);
    }

    @Override
    public List<Team> allUserTeams() {
        User currentUser = SecurityUtils.getCurrentUser();
        return repository.getUserTeams(currentUser);
    }

    @Override
    public Team createTeam(Team team) {
        Set<UserTeam> usersInTeam = team.getUsers();
        usersInTeam.add(addCurrentUserToTeamWithAdminRights(team));
        team.setUsers(usersInTeam);
        return save(team);
    }

    private UserTeam addCurrentUserToTeamWithAdminRights(Team team) {
        UserTeam userTeam = new UserTeam();
        User user = SecurityUtils.getCurrentUser();

        userTeam.setUser(user);
        userTeam.setAdminRights(true);
        userTeam.setTeam(team);

        return userTeam;
    }

    @Override
    public Team followTeam(Long id, Team team) {
        team.setId(id);
        team = save(team);
        return team;
    }

    @Override
    public Team doEditWithRestriction(Long id, Team team) {
        team = removeObservingUsersWhoAreAddedToTeam(team);
        team.setId(id);
        team = save(team);
        return team;
    }

    @Override
    public void delete(Long id) {
        Team currentTeam = findById(id);
        detachFromUser(currentTeam);
        super.delete(currentTeam);
    }

    @Override
    public Team removeObservingUsersWhoAreAddedToTeam(Team team) {
        Set<UserTeam> users = team.getUsers();
        for (UserTeam user : users) {
            if (team.getFollowingUsers().contains(user.getUser())) {
                team.getFollowingUsers().remove(user.getUser());
            }
        }
        return team;
    }

    @Override
    public Team save(Team team) {
        if (team.isPrivateTeam()) {
            team.setFollowingUsers(null);
        }

        Iterator<UserTeam> iterator = team.getUsers().iterator();
        while (iterator.hasNext()) {
            UserTeam userTeam = iterator.next();
            User dbUser = null;
            try {
                dbUser = userService.findOrCreateNewUser(userTeam.getUser().getUsername());
            } catch (UserNotFoundException e) {
                LOGGER.error(e.getMessage(), e);
                iterator.remove();
            }
            userTeam.setUser(dbUser);
        }

        return super.save(team);
    }

    @Override
    public void delete(Team team) {
        detachFromUser(team);
        super.delete(team);
    }

    @Override
    public void deleteById(Long aLong) {
        Team team = repository.findOne(aLong);
        detachFromUser(team);
        super.deleteById(aLong);
    }

    @Override
    public void deleteAll() {
        List<Team> teams = repository.findAll();
        for (Team t : teams) {
            detachFromUser(t);
        }
        super.deleteAll();
    }

    private void detachFromUser(Team team) {
        Set<User> users = team.getFollowingUsers();
        Set<UserTeam> usersInTeam = team.getUsers();

        for (User user : users) {
            if (user.getFollowedTeam().contains(team)) {
                user.getFollowedTeam().remove(team);
            }
        }
        for (UserTeam user : usersInTeam) {
            if (user.getUser().getDefaultTeam() != null && user.getUser().getDefaultTeam().getId().equals(team.getId())) {
                user.getUser().setDefaultTeam(null);
            }
        }
    }

}
