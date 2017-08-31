angular.module('mainApp').component('welcomeModal', {
    templateUrl: 'js/dashboard/welcome-modal.template.html',
    bindings: {
        close: '&'
    },
    controller: function ($timeout, templateService, $rootScope, $translate) {
        var self = this;

        self.ok = function () {
            self.close({$value: 1});
        };
    }
});