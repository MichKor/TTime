angular.module('mainApp').factory('reportService', function ($resource) {
    return $resource('/report', {}, {
        getReportsForProjectAndInterval: {
            method: 'GET',
            url: '/report/generate',
            params: {
                "startDate": "@startDate",
                "endDate": "@endDate",
                "projectId": "@projectId"
            }
        },upload: {
            method: 'POST',
            headers: {
                'Content-Type': undefined
            }
        }
    });
});
