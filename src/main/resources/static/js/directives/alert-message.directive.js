angular.module('mainApp')
    .directive('alertMessage', function($timeout, AlertMessageService, $rootScope) {
    return {
        restrict : 'E',
        templateUrl : 'js/directives/alert-message.html',
        scope : {
            errMessage : '=alertMessage'
        },
        link : function(scope, element, attrs) {
            scope.removeMessage = AlertMessageService.removeMessage;
            $timeout.cancel($rootScope.errorTimer);

            $rootScope.errorTimer = $timeout(function() {
                $rootScope.removeErrorMessage();
            }, 8000);

        }
    }
});