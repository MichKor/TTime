angular.module('mainApp')
    .component('profileEdit', {
        templateUrl: 'js/profile/edit/profile-edit.template.html',
        controller: function ($rootScope, userService, $state, $translate) {
            var self = this;

            self.languages = [
                {
                    "tag": "pl-PL",
                    "displayName": 'BUTTON_LANG_PL'
                },
                {
                    "tag": "en-US",
                    "displayName": 'BUTTON_LANG_EN'
                },
            ];

            self.notifications = $rootScope.currentUser.notificationsEnabled ? true : false;
            if ($rootScope.currentUser.localeName) {
                self.selectedLanguage = $rootScope.currentUser.localeName;
            } else {
                switch ($rootScope.appLang) {
                    case 'pl':
                        self.selectedLanguage = 'pl-PL'
                        break;
                    default:
                        self.selectedLanguage = 'en-US'
                }
            }

            self.submit = function () {
                userService.updateUserDetails({
                    "id": $rootScope.currentUser.id,
                    "notificationsEnabled": self.notifications,
                    "localeName": self.selectedLanguage
                }, function (response) {
                    $rootScope.currentUser = response;
                    $state.transitionTo('dashboard.profile-view');
                });
            }
        }
    });
