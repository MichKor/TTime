"use strict";
angular.module('mainApp')
    .run(function ($rootScope, $http, $transitions, $q, $translate) {
    
        $rootScope.appLang = $translate.use();

        var authenticationEnded = false;
        $rootScope.cameFrom = undefined;

        $rootScope.authenticated = false;

        $rootScope.authenticate = function (credentials, callback) {
            var headers = credentials ? {
                authorization: "Basic " +
                    btoa(credentials.username + ":" + credentials.password)
            } : {};

            $http.get('user', {
                headers: headers
            }).then(function (response) {
                if (response.data) {
                    $rootScope.authenticated = true;
                    $rootScope.setLoggedIn(response.data);
                } else {
                    $rootScope.authenticated = false;
                }
                callback && callback();

            }, function () {
                $rootScope.authenticated = false;
                callback && callback();
            })
        };

        $rootScope.setLoggedOut = function () {
            $rootScope.authenticated = false;
            $rootScope.currentUser = undefined;
        };

        $rootScope.setLoggedIn = function (userDetails) {
            $rootScope.authenticated = true;
            $rootScope.currentUser = userDetails;
            
            $http.get('/report/enabled')
                .then(function (response) {
                    $rootScope.jiraReportsEnabled = response.data;
                });
        };

        $rootScope.isLoggedIn = function () {
            return $rootScope.authenticated;
        };

        $rootScope.hasAuthority = function (role) {
            if ($rootScope.authenticated === false) {
                return false;
            }
            var i;
            for (i = 0; i < $rootScope.currentUser.roles.length; i++) {
                if ($rootScope.currentUser.roles[i].name === role) {
                    return true;
                }
            }
            return false;
        };

        $transitions.onExit({}, function($transition) {
            $rootScope.removeErrorMessage();
        })

        $transitions.onBefore({}, function ($transition) {
            var state = $transition.to();
            if (!authenticationEnded && state.name !== 'home') {
                $rootScope.cameFrom = state.name;
                $transition.router.stateService.transitionTo('home');
                return false;
            }
            if (state.autoRedirection !== undefined) {
                if (state.autoRedirection.isLoggedIn !== undefined && $rootScope.isLoggedIn()) {
                    $transition.router.stateService.transitionTo(state.autoRedirection.isLoggedIn);
                    return false;
                } else if (state.autoRedirection.isNotLoggedIn !== undefined && !$rootScope.isLoggedIn()) {
                    $transition.router.stateService.transitionTo(state.autoRedirection.isNotLoggedIn);
                    return false;
                }else if(state.autoRedirection.jiraReportsDisabled !== undefined && $rootScope.isLoggedIn() && !$rootScope.jiraReportsEnabled){
                    $transition.router.stateService.transitionTo(state.autoRedirection.jiraReportsDisabled);
                    return false;
                }
            }
            return true;
        });

        $rootScope.authenticationPromise = $q(function (resolve, reject) {
            $rootScope.authenticate(undefined, function () {
                authenticationEnded = true;
                resolve(true);
            });
        });

        $http.get('api/time').then(function (response) {
            var serverTime = response.data;
            $rootScope.serverTime = moment().set({
                year: serverTime[0],
                month: serverTime[1]-1,
                date: serverTime[2],
                hour: serverTime[3],
                minute: serverTime[4],
                second: serverTime[5]
            });
        },function () {
        })

        $rootScope.$on('$translateChangeSuccess', function() {
            var langKey = $translate.use();
            if (!langKey) {
                return;
            }
            
            $rootScope.appLang = langKey;
            moment.locale(langKey);
        });
    
    });
