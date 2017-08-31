angular.module('mainApp').component('editTeamModal', {
    templateUrl: '/js/teams/editTeam.template.html',
    bindings: {
        resolve: '<',
        close: '&',
        dismiss: '&'
    },
    controller: function ($scope, userService, userDefaultTeamService, teamService, $rootScope, AlertMessageService, $translate) {
        var self = this;
        var oldTeam;
        self.usersToDelete = [];
        self.team = {"users" : []};
        self.users = [];
        self.currentUser = null;
        self.renderToggle = true;

        self.$onInit = function () {
            self.team = angular.copy(self.resolve.team);
            oldTeam = angular.copy(self.resolve.team);
        };

        self.edit = function () {
            if(teamNotChanged()) {
                AlertMessageService.displayWarning('EDIT_TEAM_WARNING');
                self.dismiss({$value: 'cancel'});
            } else {
                if (userToDelete()) {
                    userDefaultTeamService.deleteDefaultTeam({"teamId": self.team.id}, self.usersToDelete);
                }
                teamService.editTeam({ "id": self.team.id }, self.team, function(success) {
                    $rootScope.removeErrorMessage();
                    AlertMessageService.displaySuccess('EDIT_TEAM_SUCCESS');
                    self.close({$value: success});
                }, function(error) {
                    $rootScope.removeErrorMessage();
                    AlertMessageService.displayError(error.data.errorMsg);
                    self.close({$value: error});
                });
            }
        }

        function teamNotChanged() {
            return angular.equals(oldTeam, self.team)
        }

        function userToDelete() {
            if(self.usersToDelete.length > 0) {
                return true;
            }
            return false;
        }

        self.addPerson = function(selectedUser) {
            var notAdded = undefined;
            var user = {"adminRights": false, "user": selectedUser};

            if(userIsSelected(selectedUser)) {
                var elementPos = self.team.users.map(function(x) {return x.user.username; }).indexOf(selectedUser.username);
                var personFound = self.team.users[elementPos];

                if(personFound === notAdded) {
                    self.team.users.push(user);
                    self.currentUser = null;
                }
            }
        }

        function userIsSelected(user) {
            if(user !== null) {
                return true;
            }
            return false;
        };

        self.changeAdminRights = function(user) {
            if(userIsPossibleToDelete(user)) {
                user.adminRights = !user.adminRights;
            } else {
                AlertMessageService.displayError('LAST_ADMIN_ERROR');
            }
        }

        self.deleteUser = function(user) {
            if(userIsPossibleToDelete(user)) {
                self.usersToDelete.push(user.user.id);
                self.team.users.splice(self.team.users.indexOf(user), 1);
            } else {
                AlertMessageService.displayError('LAST_ADMIN_ERROR');
            }
        }

        function userIsPossibleToDelete(user) {
            var countAdminsInTeam = 0;
            var lastAdminId;
            angular.forEach(self.team.users, function(user) {
                if(user.adminRights === true) {
                    countAdminsInTeam++;
                    if(countAdminsInTeam === 1) {
                        lastAdminId = user.user.id;
                    }
                }
            });

            if (countAdminsInTeam > 1) {
                return true;
            }
            else if (user.user.id !== lastAdminId) {
                return true;
            }
            else {
                return false;
            }
        }

        self.takenUsersName = function(user) {
            var takenUsersName = [];
            var notAdded = -1;
            getTakenUsersName(takenUsersName);
            return takenUsersName.indexOf(user.username) === notAdded;
        }

        function getTakenUsersName(takenUsersName) {
            takenUsersName.push($rootScope.currentUser.username);
            for (var i = 0; i < self.team.users.length; i++) {
                takenUsersName.push(self.team.users[i].user.username);
            }
            return takenUsersName;
        };


        self.cancel = function () {
            self.dismiss({$value: 'cancel'});
        };
        
        $scope.$on('userQueryChanged', function (event, query) {
            if (query.length < 3) {
                self.users = [];
                return;
            }
            userService.queryUsers({"query" : query }, function (response) {
                if (response == null) {
                    self.users = [];
                    return;
                }
                self.users = response;
            });
        });
    }
});