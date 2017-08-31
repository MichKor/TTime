angular.module('mainApp').component('dashboard', {
    templateUrl: 'js/dashboard/dashboard.template.html',
    controller:
        function ($scope, $state, userService, $rootScope, timeUtilities, $interval, $translate, userDefaultTeamService, scheduleService, $uibModal) {
            console.log('Dashboard controller');
            var self = this;
            
            self.schedules = [];
            self.modalInstance = undefined;
            
            self.updateTeams = function () {
                getDefaultTeam();
                userService.getTeams(
                    function(response) {
                        self.schedules = response.schedules;
                        self.teams = response.myTeams;
                        self.observedTeams = [];
                        angular.forEach(response.observedTeams, function (team) {
                            if (self.teams.filter(function (t) { return t.id === team.id }).length === 0) {
                                self.observedTeams.push(team);
                            }
                        });
                        angular.forEach(self.teams, function (team) {
                            doNotShowCurrentUserInTeam(team);
                        });
                    }
                );
            }

            function getDefaultTeam() {
                userDefaultTeamService.get(function(response) {
                    self.defaultTeam = response;
                    if(defaultTeamIsSet(self.defaultTeam)) {
                        doNotShowCurrentUserInTeam(self.defaultTeam);
                    }
                });
            }

            function defaultTeamIsSet(team) {
                return team.users !== undefined;
            }

            function doNotShowCurrentUserInTeam(team) {
                for (var i = 0; i < team.users.length; i++) {
                    if(team.users[i].user.id === $rootScope.currentUser.id) {
                        team.users.splice(i, 1);
                        return team;
                    }
                }
            }
            
            self.showSchedule = function (userId, userName) {
                scheduleService.showSchedule(userId, userName);
            }
            
            self.displayTime = function (userId) {
                if (self.schedules[userId] === undefined) {
                    return undefined;
                }
                var mostAppropriateInterval = undefined;
                var now = new Date();
                var userIsAlreadyGone = false;
                var i;
                for (i = 0; i < self.schedules[userId].length; i++) {
                    if (mostAppropriateInterval === undefined) {
                        mostAppropriateInterval = self.schedules[userId][i];
                
                        if (timeUtilities.compareTimes(mostAppropriateInterval.endTime, timeUtilities.dateToArrayTime(now)) < 0) {
                            userIsAlreadyGone = true;
                        }
                        else {
                            userIsAlreadyGone = false;
                        }
                    }
                    else {
                        if (userIsAlreadyGone) {
                            if (timeUtilities.compareTimes(timeUtilities.dateToArrayTime(now), self.schedules[userId][i].endTime) < 0) {
                                mostAppropriateInterval = self.schedules[userId][i];
                                userIsAlreadyGone = false;
                            }
                            else if (timeUtilities.compareTimes(mostAppropriateInterval.endTime, self.schedules[userId][i].endTime) < 0) {
                                mostAppropriateInterval = self.schedules[userId][i];
                            }
                        }
                        else {
                            if (timeUtilities.compareTimes(timeUtilities.dateToArrayTime(now), self.schedules[userId][i].endTime) < 0
                                && timeUtilities.compareTimes(self.schedules[userId][i].endTime, mostAppropriateInterval.endTime) < 0) {
                                mostAppropriateInterval = self.schedules[userId][i];
                            }
                        }
                    }
                }
                return mostAppropriateInterval;
            }
            
            self.updateTeams();
            
            $scope.$on('forcedUpdateOfTeams', function () {
                self.updateTeams();
            });
            
            self.broadcastIntervalCheck = function () {
                $scope.$broadcast('checkTimeIntervals');
            }
            
            self.timerPromise = $interval(self.broadcastIntervalCheck, 60000);
            
            $scope.$on('$destroy', function() {
                $interval.cancel(self.timerPromise);
                if (self.modalInstance) {
                    self.modalInstance.dismiss();
                }
            });
            
            self.showTeamsSchedule = function (team) {
                $state.go('dashboard.teamSchedule', { "teamId" : team.id, "teamName" : team.name }, { reload: 'dashboard.teamSchedule' });
            }
            
            self.showWelcomeModal = function () {
                self.modalInstance = $uibModal.open({
                    animation: true,
                    component: 'welcomeModal',
                    backdrop: 'static',
                    keyboard: false,
                    size: 'lg'
                });

                self.modalInstance.result.then(function () {
                    userService.setLoggedInBefore(function (response) {
                        $rootScope.currentUser = response;
                    });
                    self.modalInstance = null;
                }, function () {
                    self.modalInstance = null;
                });
            }
            
            if (!$rootScope.currentUser.loggedInBefore) {
                self.showWelcomeModal();
            }
        }
});