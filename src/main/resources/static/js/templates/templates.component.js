angular.module('mainApp').component('templates', {
    templateUrl: 'js/templates/templates.template.html',
    controller: function (userService, templateService, $uibModal, $translate, $rootScope, AlertMessageService) {
        var self = this;

        self.newTemplate = {};
        userService.getTemplates(
            function (resp) {
                if (resp.templates === undefined) {
                    self.templates = [];
                }
                else {
                    self.templates = resp.templates;
                    self.defaultTemplateId = resp.defaultId;
                }
            }
        );

        self.showEditModal = function(editedTemplate) {
            $rootScope.removeErrorMessage();
            var modalInstance = $uibModal.open({
                animation: true,
                component: 'editTemplateModal',
                resolve: {
                    template: function () {
                        return editedTemplate;
                    }
                }
            });

            modalInstance.result.then(function (newTemplate) {
                if (editedTemplate !== undefined) {
                    self.templates[self.templates.indexOf(editedTemplate)] = newTemplate;
                }
                else {
                    self.templates.push(newTemplate);
                }
            }, function () {}
            );
        }

        self.deleteModal = function (template) {
            $rootScope.removeErrorMessage();
            var modalInstance = $uibModal.open({
                animation: true,
                component: 'deleteTemplateModal'
            });

            modalInstance.result.then(function (confirm) {
                if (confirm) {
                    templateService.delete({ "id": template.id }, function (success) {
                        self.templates.splice(self.templates.indexOf(template), 1);
                        AlertMessageService.displaySuccess('DELETE_TEMPLATE_SUCCESS');
                    }, function(error) {
                        AlertMessageService.displayWarning('UNKNOWN_ERROR');
                    });
                }
            }, function () { });
        }

        self.setAsDefault = function (template) {
            userService.setDefaultTemplate({ "tempId": template.id }, function (success) {
                    self.defaultTemplateId = template.id;
                    AlertMessageService.displaySuccess('SET_DEFAULT_TEMPLATE', {name: template.name});
                }, function (error) {
                    AlertMessageService.displayWarning('UNKNOWN_ERROR');
                }
            );
        }
    }
});