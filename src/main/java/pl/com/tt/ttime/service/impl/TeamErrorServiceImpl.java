package pl.com.tt.ttime.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.com.tt.ttime.error.ErrorInformation;
import pl.com.tt.ttime.error.TeamErrors;
import pl.com.tt.ttime.model.Team;
import pl.com.tt.ttime.model.UserTeam;
import pl.com.tt.ttime.security.SecurityUtils;
import pl.com.tt.ttime.service.TeamErrorService;
import pl.com.tt.ttime.service.TeamService;
import pl.com.tt.ttime.service.UserService;

import java.util.Set;

@Service
public class TeamErrorServiceImpl implements TeamErrorService {

    private static final int MAX_TEAMS_TO_CREATE_PER_USER = 10;

    private static final TeamErrors TEAM_NOT_FOUND = TeamErrors.TEAM_NOT_FOUND;
    private static final TeamErrors ADMIN_MISSING = TeamErrors.ADMIN_MISSING;
    private static final TeamErrors PERMISSION_ERROR = TeamErrors.PERMISSION_ERROR;
    private static final TeamErrors TEAM_LEADER_MISSING = TeamErrors.TEAM_LEADER_MISSING;
    private static final TeamErrors TOO_MANY_TEAMS = TeamErrors.TOO_MANY_TEAMS;
    private static final TeamErrors OK = TeamErrors.OK;

    private TeamService teamService;
    private UserService userService;

    @Autowired
    public void setTeamService(TeamService teamService) {
        this.teamService = teamService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ErrorInformation checkErrorInformationForRestrictedUpdate(Long id, Team team) {
        Team currentTeam = teamService.findById(id);
        if (currentTeam == null) {
            return new ErrorInformation(TEAM_NOT_FOUND);
        }
        if (noAdminInTeam(team)) {
            return new ErrorInformation(ADMIN_MISSING);
        }
        if (!hasPermission(currentTeam)) {
            return new ErrorInformation(PERMISSION_ERROR);
        }
        if (noTeamLeader(team)){
            return new ErrorInformation(TEAM_LEADER_MISSING);
        }

        return new ErrorInformation(OK);
    }

    @Override
    public ErrorInformation checkErrorForFollowTeam(Long id, Team team) {
        Team currentTeam = teamService.findById(id);
        if (currentTeam == null) {
            return new ErrorInformation(TEAM_NOT_FOUND);
        }
        if (noAdminInTeam(team)) {
            return new ErrorInformation(ADMIN_MISSING);
        }
        if (noTeamLeader(team)){
            return new ErrorInformation(TEAM_LEADER_MISSING);
        }

        return new ErrorInformation(OK);
    }

    @Override
    public ErrorInformation checkErrorInformationForDeleteTeam(Long id) {
        Team currentTeam = teamService.findById(id);

        if (currentTeam == null) {
            return new ErrorInformation(TEAM_NOT_FOUND);
        }
        if (!hasPermission(currentTeam)) {
            return new ErrorInformation(PERMISSION_ERROR);
        }

        return new ErrorInformation(OK);
    }

    @Override
    public ErrorInformation checkErrorInformationForCreateTeam() {
        int numberOfTeamsWhereUserIsOwner = userService.countUserTeams();

        if(numberOfTeamsWhereUserIsOwner >= MAX_TEAMS_TO_CREATE_PER_USER) {
            return new ErrorInformation(TOO_MANY_TEAMS);
        }

        return new ErrorInformation(OK);
    }

    private boolean noTeamLeader(Team team) {
        if(team.getTeamLeader() == null) {
            return true;
        }
        return false;
    }

    private boolean noAdminInTeam(Team team) {
        Set<UserTeam> users = team.getUsers();
        for (UserTeam user : users) {
            if (user.isAdminRights()) {
                return false;
            }
        }
        return true;
    }

    private boolean hasPermission(Team currentTeam) {
        Set<UserTeam> users = currentTeam.getUsers();
        Long currentUserId = SecurityUtils.getCurrentUser().getId();

        for (UserTeam user : users) {
            if (user.getUser().getId() == currentUserId) {
                if (user.isAdminRights()) {
                    return true;
                }
            }
        }
        return false;
    }
}
