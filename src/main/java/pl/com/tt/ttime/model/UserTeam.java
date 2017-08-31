package pl.com.tt.ttime.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "user_team")
public class UserTeam implements Serializable {

    @EmbeddedId
    @JsonIgnore
    private UserTeamPK userTeamPK = new UserTeamPK();

    @Column(name = "admin_rights", nullable = true)
    private Boolean adminRights;


    public User getUser() {
        return userTeamPK.getUser();
    }

    public void setUser(User user) {
        this.userTeamPK.setUser(user);
    }

    @JsonBackReference("team_members")
    public Team getTeam() {
        return userTeamPK.getTeam();
    }

    public void setTeam(Team team) {
        this.userTeamPK.setTeam(team);
    }

    public Boolean isAdminRights() {
        return adminRights;
    }

    public void setAdminRights(Boolean adminRights) {
        this.adminRights = adminRights;
    }
}
