angular.module('mainApp').factory('templateService', function ($resource) {
    return $resource('/template/:id', {id: "@id"}, {
        update: {
            method: 'PUT'
        }
    });
});
