angular.module('mainApp').factory('HttpResponse', function ($q, $injector, $rootScope) {
    return {
        request: function (config) {
            return config || $q.when(config);
        },
        requestError: function (rejection) {
            return $q.reject(rejection);
        },
        response: function (response) {
            return response || $q.when(response);
        },
        responseError: function (rejection) {
            if (rejection.status === 401) {
                $rootScope.setLoggedOut();
                $injector.get('$state').transitionTo('login');
            }

            if (rejection.status === 404) {
                $injector.get('$state').transitionTo('home');
            }

            return $q.reject(rejection);
        }
    };
});