angular.module('mainApp').factory('utilitiesService', function ($resource) {
    return $resource('/api', {}, {
        getHolidaysByYear: {
            method: 'GET',
            url: '/api/holidays',
            isArray: true,
            params: {
                "year": "@year"
            }
        },
        getHolidaysForPresentYear: {
            method: 'GET',
            url: '/api/holidays',
            isArray: true,
            params: {
                "year": "@year"
            }
        },
    });
});
