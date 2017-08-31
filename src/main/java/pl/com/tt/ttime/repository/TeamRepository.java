package pl.com.tt.ttime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.com.tt.ttime.model.Team;
import pl.com.tt.ttime.model.User;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {

    @Query("SELECT DISTINCT t FROM User u JOIN u.userTeam ut JOIN ut.userTeamPK.team t WHERE u = :user  OR t.privateTeam = 'false'")
    List<Team> getUserTeams(@Param("user") User user);
}
