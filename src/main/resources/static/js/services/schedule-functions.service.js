angular.module('mainApp').factory('scheduleService', function ($state, $rootScope, $translate) {
    var scheduleFunctions = {};

    scheduleFunctions.showSchedule = function (userId, userName) {
        if ($rootScope.currentUser.id === userId) {
            userId = null;
            userName = null;
        }
        $state.go('dashboard.schedule', {
            "userId": userId,
            "userName": userName
        }, {
            reload: 'dashboard.schedule'
        });
    };

    scheduleFunctions.holidayRender = function (event, element) {
        if (event.rendering == 'background') {
            element.append("<div style='text-align: center; color: white;'><h4>" + $translate.instant('HOLIDAY') + "</h4></div>");
        }
    }

    return scheduleFunctions;
});
