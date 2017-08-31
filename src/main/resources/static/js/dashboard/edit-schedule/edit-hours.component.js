angular.module('mainApp').component('editHoursModal', {
    templateUrl: '/js/dashboard/edit-schedule/edit-hours.html',
    bindings: {
        resolve: '<',
        close: '&',
        dismiss: '&'
    },
    controller: function ($timeout, timeUtilities) {
        var self = this;
        self.templates = [];

        var openOffice = '6:00',
            closeOffice = '20:00';
        var startWorkDay, endWorkDay;

        self.$onInit = function () {
            self.head = self.resolve.start.format('dddd MMMM YYYY');
            self.startHour = self.resolve.start.format('H:m');
            self.endHour = self.resolve.end.format('H:m');
            startWorkDay = getNewHours(self.resolve.start, openOffice);
            endWorkDay = getNewHours(self.resolve.end, closeOffice);

            self.templates = angular.copy(self.resolve.templates);
            var dayCopy = moment(self.resolve.start);
            dayCopy.locale('en');
            var day = dayCopy.format('dddd').toUpperCase();
            angular.forEach(self.templates, function (template) {
                for (var i = template.dayWeeks.length - 1; i >= 0; i--)
                    if (day !== template.dayWeeks[i].day)
                        template.dayWeeks.splice(i, 1);
            });
        };

        self.sendTemplate = function (template) {
            self.close({
                $value: {
                    template: template,
                    date: moment(self.resolve.start)
                }
            });
        };

        self.sendAbsence = function () {
            self.close({
                $value: {
                    date: moment(self.resolve.start)
                }
            })
        };

        self.ok = function () {
            var startBusinessHours = getNewHours(self.resolve.start, self.startHour);
            var endBusinessHours = getNewHours(self.resolve.end, self.endHour);
            if (startBusinessHours.isBefore(startWorkDay)
                || endBusinessHours.isAfter(endWorkDay)
                || startBusinessHours.isSameOrAfter(endBusinessHours, 'hour')
                || moment(endBusinessHours).subtract(startBusinessHours.hour(), 'h').hour() < 1) return;
            return self.close({
                $value: {
                    start: startBusinessHours,
                    end: endBusinessHours
                }
            });
        };

        function getNewHours(oldHours, newHours) {
            var time = timeUtilities.hourStringToArrayTime(newHours);
            return moment(oldHours).hour(time[0]).minute(time[1]);
        }

        $timeout(function () {
            angular.element('.timepicker > input').timepicker({
                maxHours: 24,
                showMeridian: false
            });
        });

        self.cancel = function () {
            self.dismiss();
        };

    }
});