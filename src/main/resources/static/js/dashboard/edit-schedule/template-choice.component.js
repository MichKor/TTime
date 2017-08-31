angular.module('mainApp').component('templateChoiceModal', {
    templateUrl: '/js/dashboard/edit-schedule/template-choice.template.html',
    bindings: {
        resolve: '<',
        close: '&',
        dismiss: '&'
    },
    controller: function ($translate) {
        var self = this;
        self.templates = [];

        self.ok = function () {
            self.close({$value: 1});
        };

        self.cancel = function () {
            self.dismiss({$value: 0});
        };

        self.sendTemplate = function (template) {
            self.close({$value: template});
        }

        self.$onInit = function () {
            self.templates = self.resolve.templates;
        }
    }
});