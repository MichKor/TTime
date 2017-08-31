angular.module('mainApp').component('addTeamModal', {
    templateUrl: '/js/teams/addTeam.template.html',
    bindings: {
        resolve: '<',
        close: '&',
        dismiss: '&'
    },
    controller: function ($scope, userService, teamService, $rootScope, $translate, AlertMessageService) {
        var self = this;
        self.team = {"users" : []};
        self.users = [];
        self.currentUser = null;
        self.renderToggle = true;

        self.create = function () {
            self.team.teamLeader = $rootScope.currentUser;
            teamService.save(self.team, function(response) {
                AlertMessageService.displaySuccess('CREATE_TEAM_SUCCESS');
                self.close({$value: response});
            }, function(error) {
                AlertMessageService.displayError('TOO_MANY_TEAMS');
            });
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
            user.adminRights = !user.adminRights;
        }

        self.deleteUser = function(user) {
            self.team.users.splice(self.team.users.indexOf(user), 1);
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