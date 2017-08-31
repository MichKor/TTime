package pl.com.tt.ttime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import pl.com.tt.ttime.error.ErrorInformation;
import pl.com.tt.ttime.model.Team;
import pl.com.tt.ttime.service.TeamErrorService;
import pl.com.tt.ttime.service.TeamService;

import java.util.List;


@RestController
@RequestMapping("/team")
public class TeamController extends AbstractController<Team, TeamService> {

    private TeamErrorService teamErrorService;

    @Autowired
    public void setTeamErrorService(TeamErrorService teamErrorService) {
        this.teamErrorService = teamErrorService;
    }

    @Autowired
    public TeamController(TeamService service) {
        super(service);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping
    public @ResponseBody
    ResponseEntity<List<Team>> listAll() {
        List<Team> entities = service.allUserTeams();

        if (entities.isEmpty()) {
            return new ResponseEntity<List<Team>>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<List<Team>>(entities, HttpStatus.OK);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PutMapping("/{id}")
    public @ResponseBody
    ResponseEntity<?> update(@PathVariable Long id, @RequestBody Team team) {
        ErrorInformation errorResponse = teamErrorService.checkErrorForFollowTeam(id, team);
        if(errorResponse.getStatus() != HttpStatus.OK) {
            return new ResponseEntity<ErrorInformation>(errorResponse, errorResponse.getStatus());
        }

        team = service.followTeam(id, team);
        return new ResponseEntity<Team>(team, HttpStatus.OK);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PutMapping("/updateRestriction/{id}")
    public @ResponseBody
    ResponseEntity<?> editTeam(@PathVariable Long id, @RequestBody Team team) {
        ErrorInformation errorResponse = teamErrorService.checkErrorInformationForRestrictedUpdate(id, team);
        if(errorResponse.getStatus() != HttpStatus.OK) {
            return new ResponseEntity<ErrorInformation>(errorResponse, errorResponse.getStatus());
        }

        team = service.doEditWithRestriction(id, team);
        return new ResponseEntity<Team>(team, HttpStatus.OK);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @DeleteMapping("/{id}")
    public @ResponseBody
    ResponseEntity<?> delete(@PathVariable Long id) {
        ErrorInformation errorResponse = teamErrorService.checkErrorInformationForDeleteTeam(id);
        if(errorResponse.getStatus() != HttpStatus.OK) {
            return new ResponseEntity<ErrorInformation>(errorResponse, errorResponse.getStatus());
        }

        service.delete(id);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PostMapping
    public @ResponseBody
    ResponseEntity<?> create(@RequestBody Team team) {
        ErrorInformation errorResponse = teamErrorService.checkErrorInformationForCreateTeam();
        if(errorResponse.getStatus() != HttpStatus.OK) {
            return new ResponseEntity<ErrorInformation>(errorResponse, errorResponse.getStatus());
        }

        Team newTeam = service.createTeam(team);
        return new ResponseEntity<Team>(newTeam, HttpStatus.OK);
    }
}