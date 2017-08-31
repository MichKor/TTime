angular.module('mainApp').component('reports', {
    templateUrl: 'js/reports/reports.template.html',
    controller: function ($rootScope, $scope, $window, projectService, userService, AlertMessageService, reportService) {
        var self = this;
        self.projects = [];
        self.reports = [];
        
        if ($rootScope.hasAuthority('ROLE_ADMIN')) {
            projectService.query(function (response) {
                self.projects = response;
            }, function (responseError) {

            });

            $scope.$on('userQueryChanged', function (event, query) {
                if (query.length < 3) {
                    self.users = [];
                    return;
                }
                userService.queryUsers({
                    "query": query
                }, function (response) {
                    if (response == null) {
                        self.users = [];
                        return;
                    }
                    self.users = response;
                });
            });

            self.generate = function () {
                var selectedMonth = moment(self.selectedMonth);
                var url = '/report/generate?startDate=' + selectedMonth.format('YYYY-MM-DD') + '&endDate=' + selectedMonth.endOf('month').format('YYYY-MM-DD');
                angular.forEach(self.selectedProjects, function (p) {
                    url = url + '&projectId=' + p.id;
                });
                if (self.selectedUsers.length) {
                    url += '&userName=' + self.selectedUsers[0].username;
                    for (var i = 1; i < self.selectedUsers.length; i++) {
                        url += ',' + self.selectedUsers[i].username;
                    }
                }
                $window.location.assign(url);
            };

            self.sendReport = function () {
                var formData = new FormData();
                formData.append('file', self.file);

                reportService.upload(formData, function (response) {
                        AlertMessageService.displaySuccess('REPORTS_UPLOAD_SUCCESS');
                        self.file = undefined;
                        self.reports.push(response);
                    },
                    function (response) {
                        AlertMessageService.displayError('REPORTS_UPLOAD_ERROR');
                    }
                );
            };
        }
        
        reportService.query(function (data) {
            self.reports = data;
        }, function (response) {
            self.reports = [];
            AlertMessageService.displayError('REPORTS_QUERY_ERROR');
        });

        self.downloadReport = function (report) {
            $window.location.assign('/report/' + report.id);
        }

        $scope.popup = {
            opened: false
        };
        
        $scope.openCalendarPopup = function () {
            $scope.popup.opened = true;
        };

        $scope.dateOptions = {
            datepickerMode: 'month',
            formatYear: 'yyyy',
            maxDate: moment($rootScope.serverTime).endOf('month'),
            minMode: 'month',
            startingDay: 1,
            showButtonBar: false
        };
    }
});
