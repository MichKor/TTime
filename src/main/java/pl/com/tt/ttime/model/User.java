package pl.com.tt.ttime.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.com.tt.ttime.localization.LocaleConfig;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

@Entity
@SequenceGenerator(name = "idgen", sequenceName = "public.users_id_seq", allocationSize = 1)
@Table(name = "users")
public class User extends AbstractEntity implements UserDetails {

    @NotNull
    private String username;

    @NotNull
    private String displayName;

    @NotNull
    private String email;

    private String localeName;

    private boolean loggedInBefore = false;

    @OneToMany(mappedBy = "teamLeader", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Team> userTeams;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "default_template")
    @JsonIgnore
    private Template defaultTemplate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "default_team")
    @JsonIgnore
    private Team defaultTeam;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Day> days;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(mappedBy = "followingUsers", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Team> followedTeam;

    @OneToMany(mappedBy = "userTeamPK.user")
    @JsonIgnore
    private Set<UserTeam> userTeam;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Template> templates;

    @ManyToMany(mappedBy = "users", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Report> reports;

    @Column(name = "notifications_enabled")
    private boolean notificationsEnabled = true;

    public User() {
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        final List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (final Role role : getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return null;
    }

    @Transient
    @JsonIgnore
    public Locale getLocale() {
        if (getLocaleName() == null || getLocaleName().equals("")) {
            return LocaleConfig.getDefaultLocale();
        }
        Locale locale = Locale.forLanguageTag(getLocaleName());
        if (locale.getLanguage().equals("")) {
            return LocaleConfig.getDefaultLocale();
        }
        return locale;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocaleName() {
        return localeName;
    }

    public void setLocaleName(String localeName) {
        this.localeName = localeName;
    }

    public boolean isLoggedInBefore() {
        return loggedInBefore;
    }

    public void setLoggedInBefore(boolean loggedInBefore) {
        this.loggedInBefore = loggedInBefore;
    }

    public Template getDefaultTemplate() {
        return defaultTemplate;
    }

    public void setDefaultTemplate(Template defaultTemplate) {
        this.defaultTemplate = defaultTemplate;
    }

    public Team getDefaultTeam() {
        return defaultTeam;
    }

    public void setDefaultTeam(Team defaultTeam) {
        this.defaultTeam = defaultTeam;
    }

    public Set<Team> getUserTeams() {
        return userTeams;
    }

    public void setUserTeams(Set<Team> userTeams) {
        this.userTeams = userTeams;
    }

    public Set<Day> getDays() {
        return days;
    }

    public void setDays(Set<Day> days) {
        this.days = days;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<Team> getFollowedTeam() {
        return followedTeam;
    }

    public void setFollowedTeam(Set<Team> followedTeam) {
        this.followedTeam = followedTeam;
    }

    public Set<UserTeam> getUserTeam() {
        return userTeam;
    }

    public void setUserTeam(Set<UserTeam> userTeam) {
        this.userTeam = userTeam;
    }

    public Set<Template> getTemplates() {
        return templates;
    }

    public void setTemplates(Set<Template> templates) {
        this.templates = templates;
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public Set<Report> getReports() {
        return reports;
    }

    public void setReports(Set<Report> reports) {
        this.reports = reports;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        if (!super.equals(o)) return false;

        User user = (User) o;

        if (username != null ? !username.equals(user.username) : user.username != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (username != null ? username.hashCode() : 0);
        return result;
    }
}
