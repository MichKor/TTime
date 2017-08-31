angular.module('mainApp')
    .component('login', {
        templateUrl: 'js/login/login.template.html',
        controller: function($rootScope, $state, $http) {
            var self = this;

            self.credentials = {};
            self.login = function() {
                $rootScope.authenticate(self.credentials, function() {
                    if ($rootScope.isLoggedIn()) {
                        self.error = false;
                        $state.go('dashboard.teamSchedule', { "teamId" : null, "teamName" : null });
                    } else {
                        self.error = true;
                    }
                });
            };
        }
    });
