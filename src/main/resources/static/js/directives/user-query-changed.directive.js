angular.module('mainApp').directive('userQueryChanged', function () {
    return {
        require: 'uiSelect',
        link: function (scope, element, attrs, $select) {
            scope.$watch(function () {
                return $select.search;
            }, function (query) {
                scope.$emit('userQueryChanged', query);
            });
        }
    };
});
