angular.module('mainApp').component('teamSchedule', {
    templateUrl: 'js/teamSchedule/team-schedule.template.html',
    controller: function ($stateParams, $scope, teamService, $translate, userService, $state, userDefaultTeamService, $rootScope, scheduleService) {

        var self = this;

        var numberOfDaysToDisplay = 5;
        $scope.daysDetails = [];
        self.users = [];

        self.actualMoment = moment();

        if ($stateParams.teamId != null) {

            self.teamId = $stateParams.teamId;
            $scope.$parent.breadcrumbMessage = $stateParams.teamName;

            teamService.get({
                id: self.teamId
            }, function (response) {
                self.users = response.users;
                prepareDaysDetails();
                prepareUserDaysDetails();
            });

        } else {

            userDefaultTeamService.get(function (response) {

                self.users = response.users;

                if (typeof response.users === "undefined") {
                    $state.go('dashboard.schedule', {
                        reload: 'dashboard.schedule'
                    });
                } else {
                    self.teamId = response.id;
                    $scope.$parent.breadcrumbMessage = response.name;
                    prepareDaysDetails();
                    prepareUserDaysDetails();
                }

            });

        }

        self.getProperIntervals = function (date, user) {
            if (user.presence === undefined || user.presence.length === 0) {
                return [undefined];
            }
            var splitDate = date.split('-');
            var arrayDate = [parseInt(splitDate[0]), parseInt(splitDate[1]), parseInt(splitDate[2])];
            for (var i = 0; i < user.presence.length; i++) {
                if (self.isDateSame(arrayDate, user.presence[i].date)) {
                    if (user.presence[i].timeIntervals.length === 0) {
                        return [undefined];
                    }
                    return user.presence[i].timeIntervals;
                }
            }
            return [undefined];
        }

        self.isDateSame = function (a, b) {
            for (var i = 0; i < 3; i++) {
                if (a[i] !== b[i]) {
                    return false;
                }
            }
            return true;
        }

        function prepareDaysDetails() {

            self.actualMoment.locale("en");
            $scope.daysDetails = [];

            for (var i = 0; i < numberOfDaysToDisplay; i++) {

                if (self.actualMoment.format('dddd') === "Saturday") {
                    self.actualMoment.add(2, 'days');
                } else if (self.actualMoment.format('dddd') === "Sunday") {
                    self.actualMoment.add(1, 'days');
                }

                var oneDayDetails = {
                    name: self.actualMoment.format('dddd').toUpperCase(),
                    date: self.actualMoment.format('YYYY-MM-DD')
                }
                $scope.daysDetails.push(oneDayDetails);

                if (i === 0) {
                    self.startMoment = moment(oneDayDetails.date, 'YYYY-MM-DD');
                } else if (i === numberOfDaysToDisplay - 1) {
                    self.endMoment = moment(oneDayDetails.date, 'YYYY-MM-DD').add(1, "days");
                }

                if (self.actualMoment.format('dddd') === "Friday") {
                    self.actualMoment.add(3, 'days');
                } else {
                    self.actualMoment.add(1, 'days');
                }
            }
        }

        function prepareUserDaysDetails() {
            for (var i = 0; i < self.users.length; i++) {
                var temp = self.users[i];

                (function (user) {
                    userService.getDaysByUserIdBetweenDates({
                            "userId": user.user.id,
                            "start": self.startMoment.format('YYYY-MM-DD'),
                            "end": self.endMoment.format('YYYY-MM-DD')
                        },
                        function (response) {
                            user.presence = response;
                        }
                    );
                })(temp);
            };
        }

        self.showSchedule = function (userId, userName) {
            scheduleService.showSchedule(userId, userName);
        }

        self.loadNextWeek = function () {
            self.actualMoment = self.endMoment;
            prepareDaysDetails();
            prepareUserDaysDetails();
        }

        self.loadCurrentWeek = function () {
            self.actualMoment = moment();
            self.actualMoment.locale("en");
            prepareDaysDetails();
            prepareUserDaysDetails();
        }

        self.loadPreviousWeek = function () {
            self.actualMoment = self.startMoment.add(-7, "days");
            prepareDaysDetails();
            prepareUserDaysDetails();
        }
    }
});
