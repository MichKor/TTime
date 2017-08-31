angular.module('mainApp')
    .directive('intervalDisplay', function (timeUtilities, $timeout) {
        return {
            restrict: 'E',
            scope: {
                timeInterval: '=',
                classOnAbsence: '@',
                classOnPresence: '@',
                classOnFuturePresence: '@'
            },
            templateUrl: 'js/dashboard/interval-display.template.html',
            link: function (scope, elem, attr, ctrl) {

                scope.$on('checkTimeIntervals', function () {
                    $timeout(function () {
                        scope.$apply();
                    })
                });

                scope.getClassForInterval = function () {
                    if (scope.timeInterval === undefined) {
                        return scope.classOnAbsence;
                    }

                    var now = new Date();

                    if (timeUtilities.compareTimes(scope.timeInterval.startTime, timeUtilities.dateToArrayTime(now)) <= 0 && timeUtilities.compareTimes(timeUtilities.dateToArrayTime(now), scope.timeInterval.endTime) <= 0) {
                        return scope.classOnPresence;
                    } else if (timeUtilities.compareTimes(timeUtilities.dateToArrayTime(now), scope.timeInterval.startTime) < 0) {
                        return scope.classOnFuturePresence;
                    } else {
                        return scope.classOnAbsence;
                    }
                }
            }
        }
    })
