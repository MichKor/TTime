angular.module('mainApp').component('deleteTemplateModal', {
    templateUrl: 'js/templates/delete-template-modal.template.html',
    bindings: {
        close: '&',
        dismiss: '&'
    },
    controller: function ($timeout, templateService, $rootScope, $translate) {
        var self = this;

        self.ok = function () {
            self.close({$value: 1});
        };

        self.cancel = function () {
            self.dismiss({$value: 'cancel'});
        };
    }
});