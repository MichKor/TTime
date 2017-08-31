"use strict";
angular.module('mainApp')
    .directive('validateSubmit', function ($translate) {
        return {
            restrict: 'A',
            require: '^form',
            scope: {
                "tooltipConfig": "=?"
            },
            link: function (scope, elem, attr, ctrl) {

                elem.attr('novalidate', '');

                var self = this;
                
                if (scope.tooltipConfig === undefined) {
                    scope.tooltipConfig = {
                        "placement": "right"
                    }
                }

                self.hideAllTooltips = function () {
                    angular.forEach(elem, function (form) {
                        angular.forEach(form, function (input) {
                            self.hideTooltip(input);
                        });
                    });
                }

                self.showTooltip = function (element, message) {
                    angular.element(element).attr('data-toggle', 'tooltip');
                    angular.element(element).attr('data-trigger', 'manual');
                    angular.element(element).attr('title', $translate.instant(message));
                    angular.element(element).tooltip(scope.tooltipConfig);
                    angular.element(element).tooltip('fixTitle')
                        .tooltip('show');
                }

                self.hideTooltip = function (element) {
                    angular.element(element).tooltip('hide');
                    angular.element(element).removeClass('validation-error');
                }

                self.tooltipsToDisplay = [];

                elem.bind('submit', function () {
                    if (ctrl.$valid) {
                        self.hideAllTooltips();
                        scope.$parent.$broadcast('hideErrorSection');
                        return scope.$parent.$apply(attr.validateSubmit);
                    } else {
                        var errorMessages = [];
                        angular.forEach(elem, function (form) {
                            angular.forEach(form, function (input) {
                                if (input.type === 'submit') {
                                    return;
                                }
                                var element = input;
                                var name = input.name;
                                if (angular.element(input).hasClass('ui-select-search') && name === '') {
                                    var elem = angular.element(input).parent().parent('.ui-select-container');
                                    if (elem != null) {
                                        name = elem.attr('name');
                                        element = elem;
                                    }
                                }
                                if (name !== undefined && name !== '') {
                                    if (ctrl[name] !== undefined && ctrl[name].$invalid) {
                                        if (ctrl[name].$error.required) {
                                            errorMessages.push({
                                                "message": "VALIDATION_ERROR_REQUIRED",
                                                "name": name,
                                                "element": element,
                                                "inputElement": input
                                            });
                                        } else if (ctrl[name].$error.number) {
                                            errorMessages.push({
                                                "message": "VALIDATION_ERROR_NUMBER",
                                                "name": name,
                                                "element": element,
                                                "inputElement": input
                                            });
                                        } else if (ctrl[name].$error.min || ctrl[name].$error.max) {
                                            errorMessages.push({
                                                "message": "VALIDATION_ERROR_MIN_MAX",
                                                "name": name,
                                                "element": element,
                                                "inputElement": input
                                            });
                                        } else if (ctrl[name].$error.pattern) {
                                            errorMessages.push({
                                                "message": "VALIDATION_ERROR_REGEX",
                                                "name": name,
                                                "element": element,
                                                "inputElement": input
                                            });
                                        }
                                    } else {
                                        self.hideTooltip(input);
                                    }
                                }
                            });
                        });
                        self.tooltipsToDisplay = errorMessages;
                        scope.$parent.$broadcast('displayErrorSection', errorMessages);
                    }
                });

                scope.$parent.$on('errorMessageDisplayed', function () {
                    angular.forEach(self.tooltipsToDisplay, function (obj) {
                        self.showTooltip(obj.element, obj.message);
                        angular.element(obj.element).addClass('validation-error');
                    });
                })
            }
        };
    });
