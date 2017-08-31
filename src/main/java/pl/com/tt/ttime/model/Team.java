package pl.com.tt.ttime.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@SequenceGenerator(name = "idgen", sequenceName = "public.team_id_seq", allocationSize = 1)
public class Team extends AbstractEntity {

    @NotNull
    private String name;

    private boolean privateTeam;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_leader")
    private User teamLeader;

    @OneToMany(mappedBy = "userTeamPK.team", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonManagedReference("team_members")
    private Set<UserTeam> users;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "followed_team", joinColumns = @JoinColumn(name = "team_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    private Set<User> followingUsers;

    public Team() {
    }

    public Set<User> getFollowingUsers() {
        return followingUsers;
    }

    public void setFollowingUsers(Set<User> followingUsers) {
        this.followingUsers = followingUsers;
    }

    public Set<UserTeam> getUsers() {
        return this.users;
    }

    public void setUsers(Set<UserTeam> users) {
        this.users = users;
    }

    public User getTeamLeader() {
        return teamLeader;
    }

    public void setTeamLeader(User teamLeader) {
        this.teamLeader = teamLeader;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPrivateTeam() {
        return privateTeam;
    }

    public void setPrivateTeam(boolean privateTeam) {
        this.privateTeam = privateTeam;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Team team = (Team) o;

        if (privateTeam != team.privateTeam) return false;
        return name != null ? name.equals(team.name) : team.name == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (privateTeam ? 1 : 0);
        return result;
    }
}