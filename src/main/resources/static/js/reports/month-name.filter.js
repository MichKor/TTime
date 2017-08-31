angular.module('mainApp')
    .filter('monthName', function () {
        return function (n) {
            return moment(n.date).format('MMMM');
        };
    });