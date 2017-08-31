angular.module('mainApp').factory('teamService', function ($resource) {
    return $resource('/team/:id', {id: "@id"}, {
        update: {
            method: 'PUT'
        },
        editTeam: {
            method: 'PUT',
            url: '/team/updateRestriction/:id'
        }
    });
});
