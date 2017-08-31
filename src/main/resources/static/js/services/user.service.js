angular.module('mainApp').factory('userService', function ($resource) {
    return $resource('/user', {id: "@id"}, {
        getTeams: {
            method: 'GET',
            url: '/user/teams'
        },
        getUsers: {
            method: 'GET',
            isArray: true,
            url: '/user/users'
        },
        update: {
            method: 'PUT'
        },
        getTemplates: {
            method: 'GET',
            url: '/user/templates'
        },
        setDefaultTemplate: {
            method: 'PUT',
            url: '/user/default-template',
            params: {
                "tempId": "@tempId"
            }
        },
        getDays: {
            method: 'GET',
            url: '/user/days',
            isArray: true
        },
        getDaysByUserId: {
            method: 'GET',
            url: '/user/days/userId',
            params: {
                "userId": "@userId"
            },
            isArray: true
        },
        getDaysByUserIdBetweenDates: {
            method: 'GET',
            url: '/user/days/userId/between',
            params: {
                "userId": "@userId",
                "start": "@start",
                "end": "@end"
            },
            isArray: true
        },
        updateUserDetails: {
            method: 'PUT',
            url: '/user/details'
        },
        getDaysByUserIdAndDate: {
            method: 'GET',
            url: '/user/days/userId/date',
            params: {
                "userId": "@userId",
                "date": "@date"
            }
        },
        queryUsers: {
            method: 'GET',
            isArray: true,
            url: '/user/search',
            params: {
                "query": "@query"
            }
        },
        setLoggedInBefore: {
            method: 'PUT',
            url: '/user/loggedInBefore'
        }
    });
});
