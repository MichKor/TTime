angular.module('mainApp')
    .directive('timeInterval', function (timeUtilities, $timeout, $translate) {
        return {
            restrict: 'E',
            scope: {
                "interval": "="
            },
            templateUrl: 'js/directives/time-interval.template.html',
            transclude: true
        }
    })
