package pl.com.tt.ttime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import pl.com.tt.ttime.exception.UserNotAuthenticatedException;
import pl.com.tt.ttime.model.Team;
import pl.com.tt.ttime.service.TeamService;
import pl.com.tt.ttime.service.UserDefaultTeamService;

import javax.websocket.server.PathParam;
import java.util.Set;

@RestController
@RequestMapping("/user/default-team")
public class UserDefaultTeamController {

    private UserDefaultTeamService userDefaultTeamService;
    private TeamService teamService;

    @Autowired
    public void setUserDefaultTeamService(UserDefaultTeamService userDefaultTeamService) {
        this.userDefaultTeamService = userDefaultTeamService;
    }

    @Autowired
    public void setTeamService(TeamService teamService) {
        this.teamService = teamService;
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping
    public @ResponseBody
    ResponseEntity<Team> getDefaultTeam() {
        Team defaultTeam = null;

        try {
            defaultTeam = userDefaultTeamService.getUserDefaultTeam();
        } catch (UserNotAuthenticatedException e) {
            return new ResponseEntity<Team>(HttpStatus.FORBIDDEN);
        }

        if (defaultTeam == null) {
            return new ResponseEntity<Team>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<Team>(defaultTeam, HttpStatus.OK);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PutMapping
    public ResponseEntity<Void> setDefaultTeam(@PathParam("team-id") Long teamId) {
        Team team = teamService.findById(teamId);

        if (team == null) {
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        try {
            userDefaultTeamService.setUserDefaultTeam(team);
        } catch (UserNotAuthenticatedException e) {
            return new ResponseEntity<Void>(HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PutMapping(value = "/deleteSingle")
    public ResponseEntity<Void> deleteSingleUserDefaultTeam(@PathParam("team-id") Long teamId) {
        Team team = teamService.findById(teamId);

        if (team == null) {
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        try {
            userDefaultTeamService.deleteDefaulTeam(team);
        } catch (UserNotAuthenticatedException e) {
            return new ResponseEntity<Void>(HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PutMapping(value = "/delete")
    public ResponseEntity<Void> deleteDefaultTeam(@PathParam("team-id") Long teamId, @RequestBody Set<Long> userId) {
        Team team = teamService.findById(teamId);

        if (team == null) {
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        try {
            userDefaultTeamService.deleteDefaulTeam(team, userId);
        } catch (UserNotAuthenticatedException e) {
            return new ResponseEntity<Void>(HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
