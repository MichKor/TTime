package pl.com.tt.ttime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.com.tt.ttime.model.Team;
import pl.com.tt.ttime.model.User;

import java.util.List;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    @Query("SELECT u FROM User u JOIN FETCH u.defaultTemplate WHERE u.defaultTemplate != null")
    Set<User> findUsersWithDefaultTemplate();

    @Query("SELECT t FROM User u JOIN u.userTeam ut JOIN ut.userTeamPK.team t WHERE u = :user ORDER BY t.name")
    List<Team> getUsersTeams(@Param("user") User user);

    @Query("SELECT t FROM User u JOIN u.followedTeam t WHERE u = :user ORDER BY t.name")
    List<Team> getUsersObservedTeams(@Param("user") User user);

    @Query("SELECT COUNT(u) FROM User u JOIN u.userTeams WHERE u = :user")
    Integer countUserTeams(@Param("user") User user);

}
