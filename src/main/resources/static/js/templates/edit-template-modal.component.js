angular.module('mainApp').component('editTemplateModal', {
    templateUrl: 'js/templates/edit-template-modal.template.html',
    bindings: {
        resolve: '<',
        close: '&',
        dismiss: '&'
    },
    controller: function ($timeout, templateService, $rootScope, timeUtilities, $translate, AlertMessageService) {
        var self = this;
        var oldTemplate;
        
        self.weekDays = [
            { localeName: $translate.instant('MONDAY'), code: 'MONDAY' },
            { localeName: $translate.instant('TUESDAY'), code: 'TUESDAY' },
            { localeName: $translate.instant('WEDNESDAY'), code: 'WEDNESDAY' },
            { localeName: $translate.instant('THURSDAY'), code: 'THURSDAY' },
            { localeName: $translate.instant('FRIDAY'), code: 'FRIDAY' }
        ];
        self.curTemplate = { "dayWeeks" : [] };
        
        self.restoreTimeDefaults = function () {
            self.selectedDay = self.weekDays[0].code;
            self.startHour = "8:00";
            self.endHour = "16:00";
        };
        
        self.restoreTimeDefaults();
        
        self.$onInit = function () {
            if (self.resolve.template === undefined) {
                self.editMode = false;
            }
            else {
                self.editMode = true;
                self.curTemplate = angular.copy(self.resolve.template);
                oldTemplate = angular.copy(self.resolve.template);
            }
        };

        self.ok = function () {
            for (var i = 0; i < self.curTemplate.dayWeeks.length; i++) {
                if (self.curTemplate.dayWeeks[i].timeIntervals.length === 0) {
                    self.curTemplate.dayWeeks.splice(i, 1);
                    i--;
                }
            }
            if (self.editMode) {
                if(templateNotChanged()) {
                    AlertMessageService.displayWarning('EDIT_TEAM_WARNING');
                    self.dismiss({$value: 'cancel'});
                } else {
                    templateService.update({ "id": self.curTemplate.id }, self.curTemplate, function (success) {
                        $rootScope.removeErrorMessage();
                        AlertMessageService.displaySuccess('EDIT_TEMPLATE_SUCCESS');
                        self.close({$value: success});
                    }, function(error) {
                        $rootScope.removeErrorMessage();
                        AlertMessageService.displayError('UNKNOWN_ERROR');
                    });
                }
            }
            else {
                templateService.save(self.curTemplate, function (success) {
                    $rootScope.removeErrorMessage();
                    AlertMessageService.displaySuccess('CREATE_TEMPLATE_SUCCESS');
                    self.close({$value: success});
                }, function(error) {
                    $rootScope.removeErrorMessage();
                    AlertMessageService.displayError('UNKNOWN_ERROR');
                });
            }
        };

        function templateNotChanged() {
            return angular.equals(oldTemplate, self.curTemplate)
        }

        self.cancel = function () {
            self.dismiss({$value: 'cancel'});
        };
        
        self.addTimeInterval = function () {
            var startTime = timeUtilities.hourStringToArrayTime(self.startHour);
            var endTime = timeUtilities.hourStringToArrayTime(self.endHour);
            if (timeUtilities.compareTimes(endTime, startTime) < 0) {
                AlertMessageService.displayError('EARLIER_THAN_START');
                return;
            }
            if (startTime[0] === endTime[0] && startTime[1] === endTime[1]) {
                AlertMessageService.displayError('THE_SAME_TIMES');
                return;
            }
            var interval = { "startTime": startTime, "endTime": endTime };
            var i, j;
            for (i = 0; i < self.curTemplate.dayWeeks.length; i++) {
                if (self.curTemplate.dayWeeks[i].day === self.selectedDay) {
                    if (self.checkCollisions(self.curTemplate.dayWeeks[i].timeIntervals, interval)) {
                        AlertMessageService.displayError('COLLISION_TIMES');
                        return;
                    }
                    self.pushTimeInterval(self.curTemplate.dayWeeks[i].timeIntervals, interval);
                    return;
                }
            }
            var dayWeek = {
                "day": self.selectedDay,
                "timeIntervals": [ interval ]
            }
            self.curTemplate.dayWeeks.push(dayWeek);
        };
        
        self.checkCollisions = function (timeIntervals, interval) {
            var a, b, tmp;
            var i;
            for (i = 0; i < timeIntervals.length; i++) {
                a = timeIntervals[i];
                b = interval;
                
                if (timeUtilities.compareTimes(b.startTime, a.startTime) < 0) {
                    tmp = a;
                    a = b;
                    b = tmp;
                }
                
                if (timeUtilities.compareTimes(b.startTime, a.endTime) < 0) {
                    return true;
                }
            }
            
            return false;
        }
        
        self.pushTimeInterval = function (timeIntervals, interval) {
            var i, tmp, a, b;
            for (i = 0; i < timeIntervals.length; i++) {
                a = timeIntervals[i];
                b = interval;
                
                if (timeUtilities.compareTimes(a.startTime, b.endTime) == 0) {
                    b.endTime = a.endTime;
                    timeIntervals.splice(i, 1);
                    i--;
                    continue;
                }
                
                if (timeUtilities.compareTimes(b.startTime, a.endTime) == 0) {
                    b.startTime = a.startTime;
                    timeIntervals.splice(i, 1);
                    i--;
                    continue;
                }
            }
            timeIntervals.push(interval);
        }
        
        $timeout(function() {
            angular.element('.timepicker > input').timepicker({
                maxHours: 24,
                showMeridian: false
            });
        });
        self.changeLanguage = function (langKey) {
            console.log("changing language to: " + langKey);
            $translate.use(langKey);
        };
    }
});