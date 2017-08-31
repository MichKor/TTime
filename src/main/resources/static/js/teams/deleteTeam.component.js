angular.module('mainApp').component('deleteTeamModal', {
    templateUrl: '/js/teams/deleteTeam.template.html',
    bindings: {
        close: '&',
        dismiss: '&'
    },
    controller: function ($translate) {
        var self = this;

        self.ok = function () {
            self.close({$value: 1});
        };

        self.cancel = function () {
            self.dismiss({$value: 'cancel'});
        };
    }
});