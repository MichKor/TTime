angular.module('mainApp').factory('loaderInterceptor', ['$timeout', '$q', function($timeout, $q) { 
    angular.element(".loader-background").addClass("hidden");
    
    var self = this;
    
    self.awaitingRequests = [];
    self.timeout = null;
    self.nextId = 1;
    self.setupTimeout = function(config) {
        config.requestId = self.nextId++;
        self.awaitingRequests.push({"id" : config.requestId});
        if (self.timeout == null) {
            self.timeout = $timeout(
                function () { 
                    angular.element(".loader-background").removeClass("hidden");
                    self.timeout = null;
                }, 300);
        }  
        return config;
    }
    self.resolveResponse = function (response) {
        var i;
        for (i = 0; i < self.awaitingRequests.length; i++) {
            if (self.awaitingRequests[i].id === response.config.requestId) {
                var req = self.awaitingRequests.splice(i, 1)[0];
                if (self.awaitingRequests.length === 0) {
                    if (self.timeout != null) { 
                        $timeout.cancel(self.timeout);
                        self.timeout = null;
                    }
                    angular.element(".loader-background").addClass("hidden");
                }
                break;
            }
        }
        return response;
    }
    var timestampMarker = {
        request: function(config) {
            return self.setupTimeout(config);
        },
        response: function(response) {
            return self.resolveResponse(response);
        },
        responseError: function(response) {
            return $q.reject(self.resolveResponse(response));
        }
    };
    return timestampMarker;
}]);