angular.module('mainApp').component('teams', {
    templateUrl: 'js/teams/teams.template.html',
    controller:
        function ($rootScope, teamService, userDefaultTeamService, $uibModal, $scope, AlertMessageService, $translate) {
            var self = this;
            self.team = {"users" : []};
            self.loggedInUserId = $rootScope.currentUser.id;

            userDefaultTeamService.get(function (response) {
                self.defaultTeamId = response.id;
            });

            teamService.query(function(response) {
                self.teams = response;
            });

            self.addTeamModal = function() {
                $rootScope.removeErrorMessage();
                var modalInstance = $uibModal.open({
                    animation: true,
                    component: 'addTeamModal'
                });

                modalInstance.result.then(function(team) {
                    if(self.teams === undefined) {
                        self.teams = [];
                    }

                    self.teams.push(team);
                    $scope.$emit('forcedUpdateOfTeams');
                }, function () {
                    $rootScope.removeErrorMessage();
                } );
            }

            self.editTeamModal = function(editedTeam) {
                $rootScope.removeErrorMessage();
                var modalInstance = $uibModal.open({
                    animation: true,
                    component: 'editTeamModal',
                    resolve: {
                        team: function () {
                            return editedTeam;
                        }
                    }
                });

                modalInstance.result.then(function(newteam) {
                    userDefaultTeamService.get(function (response) {
                        self.defaultTeamId = response.id;
                    });
                    self.teams = teamService.query();
                    $scope.$emit('forcedUpdateOfTeams');
                }, function () {
                } );
            }

            self.deleteTeamModal = function(team) {
                $rootScope.removeErrorMessage();
                var modalInstance = $uibModal.open({
                    animation: true,
                    component: 'deleteTeamModal'
                });

                modalInstance.result.then(function(confirm) {
                    if (confirm) {
                        teamService.remove({ "id": team.id }, function (success) {
                            self.teams.splice(self.teams.indexOf(team), 1);
                            AlertMessageService.displaySuccess('DELETE_TEAM_SUCCESS');
                            $scope.$emit('forcedUpdateOfTeams');
                        }, function (error) {
                            AlertMessageService.displayError('DELETE_TEAM_ERROR');
                        });
                    }
                }, function () {} );
            }

            self.setAsDefault = function (team) {
                userDefaultTeamService.setDefaultTeam({ "teamId": team.id }, function (success) {
                        self.defaultTeamId = team.id;
                        $scope.$emit('forcedUpdateOfTeams');
                        AlertMessageService.displaySuccess('SET_DEFAULT_TEAM', {name: team.name});
                    }, function(error) {
                        AlertMessageService.displayWarning('UNKNOWN_ERROR');
                    }
                );
            }

            self.followTeam = function(team) {
                if(team.followingUsers == null) {
                    team.followingUsers = [];
                }

                team.followingUsers.push($rootScope.currentUser);

                teamService.update({"id" :team.id}, team, function(success) {
                    $scope.$emit('forcedUpdateOfTeams');
                    AlertMessageService.displaySuccess('FOLLOW_TEAM_SUCCESS', {name: team.name});
                }, function(error) {
                    team.followingUsers.splice(team.followingUsers.indexOf(team), 1);
                    AlertMessageService.displayError('UNKNOWN_ERROR');
                });
            }

            self.unfollowTeam = function(team) {
                team.followingUsers = team.followingUsers.filter(function(user) {
                    return user.id !== self.loggedInUserId;
                });

                teamService.update({"id" :team.id}, team, function(success) {
                    AlertMessageService.displaySuccess('UNFOLLOW_TEAM_SUCCESS', {name: team.name});
                    $scope.$emit('forcedUpdateOfTeams');
                }, function(error) {
                    AlertMessageService.displayError('UNKNOWN_ERROR');
                });
            }

            self.leaveTeam = function(team) {

                if(self.loggedInUserId === team.teamLeader.id) {
                    team.teamLeader = null;
                }

                if(userIsPossibleToLeave(team)) {
                     team.users = team.users.filter(function(user) {
                         return user.user.id !== self.loggedInUserId;
                     });

                     userDefaultTeamService.deleteSingleUserDefaultTeam({"teamId": team.id});
                     self.defaultTeamId = userDefaultTeamService.get();

                     teamService.update({"id" :team.id}, team, function(success) {
                        AlertMessageService.displaySuccess('LEAVE_TEAM_SUCCESS', {name: team.name});
                        $scope.$emit('forcedUpdateOfTeams');
                     }, function(error) {
                        AlertMessageService.displayError('LEAVE_TEAM_ADMIN_ERROR');
                     });
                }
                else {
                    AlertMessageService.displayError('LEAVE_TEAM_ERROR');
                }
            }

            function userIsPossibleToLeave(team) {
                var countAdminsInTeam = 0;
                var lastAdminId;
                angular.forEach(team.users, function(user) {
                    if(user.adminRights === true) {
                        countAdminsInTeam++;
                    }
                });

                if (countAdminsInTeam > 1) {
                    return true;
                }
                else if (userIsNotAdmin(team)) {
                    return true;
                }
                else {
                    return false;
                }
            }

            function userIsNotAdmin(team) {
                for(var i=0; i<team.users.length; i++) {
                    if(team.users[i].adminRights === true) {
                        if(team.users[i].user.id === self.loggedInUserId) {
                            return false;
                        }
                    }
                }
                return true;
            }

            self.hasUserAdminRights = function(team) {
                for(var i=0; i<team.users.length; i++) {
                    if(team.users[i].user.id === self.loggedInUserId) {
                        if(team.users[i].adminRights === true) {
                            return true;
                        }
                    }
                }
                return false;
            }

            self.notObserving = function(team) {
                if(team.followingUsers == null) {
                    team.followingUsers = [];
                }

                for(var i=0; i<team.followingUsers.length; i++) {
                    if(team.followingUsers[i].id === self.loggedInUserId) {
                        return false;
                    }
                }

                for(var i=0; i<team.users.length; i++) {
                    if(team.users[i].user.id === self.loggedInUserId) {
                        return false;
                    }
                }
                return true;
            }

            self.isUserAlreadyObserving = function(team) {
                for(var i=0; i<team.followingUsers.length; i++) {
                    if(team.followingUsers[i].id === self.loggedInUserId) {
                        return true;
                    }
                }
                return false;
            }

            self.isUserAlreadyInTeam = function(team) {
                for(var i=0; i<team.users.length; i++) {
                    if((team.users[i].user.id === self.loggedInUserId)) {
                        return true;
                    }
                }
                return false;
            }

            self.isTeamLeader = function(team) {
                if(self.loggedInUserId === team.teamLeader.id) {
                    return true;
                }
                return false;
            }

        }
});