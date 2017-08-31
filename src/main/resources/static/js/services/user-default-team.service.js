angular.module('mainApp').factory('userDefaultTeamService', function ($resource) {
    return $resource('user/default-team', {id: "@id"}, {
        setDefaultTeam: {
            method: 'PUT',
            params: {
                "teamId": "@teamId"
            }
        },
        deleteDefaultTeam: {
            method: 'PUT',
            url: 'user/default-team/delete',
            params: {
                "teamId": "@teamId"
            }
        },
        deleteSingleUserDefaultTeam: {
            method: 'PUT',
            url: 'user/default-team/deleteSingle',
            params: {
                "teamId": "@teamId"
            }
        }
    });
});
