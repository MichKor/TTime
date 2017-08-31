angular.module('mainApp')
    .directive('timeDisplay', function ($translate) {
    return {
        restrict: 'E',
        scope: {
            day: '@',
            removable: '@',
            timeIntervals: '='
        },
        templateUrl: 'js/templates/time-display.template.html',
        controller: function ($scope) {

            $scope.$watchCollection('timeIntervals', function() {
                if ($scope.timeIntervals !== undefined) {
                    var i;
                    for (i = 0; i < $scope.timeIntervals.length; i++) {
                        if ($scope.timeIntervals[i].day === $scope.day) {
                            $scope.times = $scope.timeIntervals[i].timeIntervals;
                            break;
                        }
                    }
                }
                else {
                    $scope.times = [];
                }
            });
            
            $scope.removeTime = function(time) {
                $scope.times.splice($scope.times.indexOf(time), 1);
                $scope.timeIntervals = $scope.timeIntervals;
            }
        }
    }
})