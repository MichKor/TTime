package pl.com.tt.ttime.service;

import pl.com.tt.ttime.error.ErrorInformation;
import pl.com.tt.ttime.model.Team;

public interface TeamErrorService {
    ErrorInformation checkErrorInformationForRestrictedUpdate(Long id, Team team);

    ErrorInformation checkErrorForFollowTeam(Long id, Team team);

    ErrorInformation checkErrorInformationForDeleteTeam(Long id);

    ErrorInformation checkErrorInformationForCreateTeam();
}
