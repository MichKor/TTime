angular.module('mainApp')
    .controller('mainController', function($rootScope, $scope, $http, $state, $translate) {
    $scope.logout = function () {
        $http({ method: 'POST', url: 'logout' }).finally(function() {
            $rootScope.setLoggedOut();
            $state.transitionTo('home');
        });
    }
    $scope.changeLanguage = function (langKey) {
        console.log("changing language to: " + langKey);
        $translate.use(langKey).then(function () {
            $rootScope.appLang = langKey;
            moment.locale(langKey);
        });
    }
});