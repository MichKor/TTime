angular.module('mainApp').factory('projectService', function ($resource) {
    return $resource('/project', {},{});
});
