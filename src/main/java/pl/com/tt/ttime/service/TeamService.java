package pl.com.tt.ttime.service;

import pl.com.tt.ttime.model.Team;

import java.util.List;

public interface TeamService extends AbstractService<Team, Long> {

    Team removeObservingUsersWhoAreAddedToTeam(Team team);
    Team doEditWithRestriction(Long id, Team team);
    Team followTeam(Long id, Team team);
    Team createTeam(Team team);

    void delete(Long id);

    List<Team> allUserTeams();
}
