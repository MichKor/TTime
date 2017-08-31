"use strict";
angular.module('mainApp')
    .directive('validatorError', function ($translate) {
    return {
        restrict: 'E',
        templateUrl: 'js/directives/validator-error.template.html',
        scope: true,
        controller: function ($scope, $timeout) {
            var self = this;
            
            self.messages = [];
            
            $scope.$on('hideErrorSection', function (event) {
                self.element.attr('hidden', '');
                self.messages = [];
            });
            
            $scope.$on('displayErrorSection', function (event, args) {
                self.messages = args;
                $timeout(function () {
                    $scope.$apply();
                    self.element.removeAttr('hidden');
                    $scope.$emit('errorMessageDisplayed', self.element);
                })
            });
            
            self.focusElement = function (element) {
                $timeout(function () {
                   element.focus(); 
                });
            }
        },
        link: 
            function (scope, elem, attr, ctrl) {
                elem.attr('hidden', '');
                
                ctrl.element = elem;
                
                scope.$ctrl = ctrl;
            }
    };
});