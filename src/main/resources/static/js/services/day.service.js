angular.module('mainApp').factory('dayService', function ($resource) {
    return $resource('/day', {},{
        saveDays: {
            method: 'POST',
            url: '/day/multiply',
            isArray: true
        }
    });
});
