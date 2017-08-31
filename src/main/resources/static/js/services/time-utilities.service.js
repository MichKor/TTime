angular.module('mainApp').factory('timeUtilities', function () {
    var self = {};

    self.dateToArrayTime = function (date) {
        return [date.getHours(), date.getMinutes()];
    };

    self.compareTimes = function (time1, time2) {
        if (time1[0] == time2[0] && time1[1] == time2[1]) {
            return 0;
        }
        if (time1[0] < time2[0]) {
            return -1;
        }
        if (time1[0] > time2[0]) {
            return 1;
        }
        if (time1[1] < time2[1]) {
            return -1;
        }
        if (time1[1] > time2[1]) {
            return 1;
        }
    };

    self.hourStringToArrayTime = function (hour) {
        var parts = hour.split(':');
        return [parseInt(parts[0]), parseInt(parts[1])];
    };

    return self;
});
