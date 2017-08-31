angular.module('mainApp').component('home', {
    templateUrl: 'js/home/home.template.html',
    controller: function ($scope, $rootScope, $state, userService) {
        console.log('Home controller');

        var self = this;
        
        if ($rootScope.cameFrom !== undefined) {
            var stateName = $rootScope.cameFrom;
            $rootScope.cameFrom = undefined;
            $state.transitionTo(stateName);
            return;
        }
        else if ($rootScope.isLoggedIn()) {
            $state.transitionTo($scope.$parent.$resolve.autoRedirection.isLoggedIn);
            return;
        }
    }
});