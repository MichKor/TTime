angular.module('mainApp').component('schedule', {
    templateUrl: 'js/schedule/schedule.template.html',
    controller: function ($rootScope, $stateParams, $scope, userService, $state, $translate, uiCalendarConfig, scheduleService, utilitiesService) {

        var self = this;

        if ($stateParams.userId != null) {
            self.userId = $stateParams.userId;
            $scope.$parent.breadcrumbMessage = "SCHEDULE_FOR";
            $scope.$parent.userName = $stateParams.userName;
        } else {
            self.userId = $rootScope.currentUser.id;
            $scope.$parent.breadcrumbMessage = "MY_SCHEDULE";
        }

        $scope.presenceDays = [];
        $scope.holidays = [];
        $scope.presenceDaysSources = [{
                events: $scope.presenceDays
        }, {
                events: $scope.holidays
            }
        ];

        $calendar = $('[ui-calendar]');

        $scope.actualView = 'MONTH';

        $scope.calendarTitle = "";

        var startMoment, endMoment;
        var firstYear, lastYear;

        $scope.changeView = function (viewCalendar, viewString) {
            $scope.actualView = viewString;
            $calendar.fullCalendar('changeView', viewCalendar);
        };

        var lang = $translate.use();

        /* config object */
        $scope.uiConfig = {
            calendar: {
                locale: lang,
                lang: lang,
                height: '100%',
                editable: false,
                header: {
                    left: '',
                    center: '',
                    right: ''
                },
                firstDay: '1',
                hiddenDays: [0, 6],
                defaultView: 'month',
                displayEventEnd: 'true',
                timeFormat: 'H:mm',
                minTime: "06:00:00",
                maxTime: "20:00:00",
                viewRender: viewRenderWithCachingEvents,
                eventRender: scheduleService.holidayRender
            }
        };

        self.getActuallyDate = function () {
            $calendar.fullCalendar('getDate');
        };

        self.openEditSchedule = function () {
            $state.go('dashboard.editSchedule', {
                currentDate: $calendar.fullCalendar('getDate')
            })
        };

        function viewRenderWithRefreshingEvents(view, element) {

            $scope.calendarTitle = view.title;

            if (!isChangedLang) {
                startMoment = view.start;
                endMoment = view.end;
                $scope.presenceDays.length = 0;
                loadDaysBetween(startMoment.format('YYYY-MM-DD'), endMoment.format('YYYY-MM-DD'));
            } else {
                isChangedLang = false;
                $scope.uiConfig.calendar.defaultDate = calendarCurrDate;
                $scope.uiConfig.calendar.defaultView = viewType;
            }
        }

        function viewRenderWithCachingEvents(view, element) {

            $scope.calendarTitle = view.title;

            if (startMoment === undefined) {
                startMoment = moment(view.start);
                endMoment = moment(view.end);
                firstYear = startMoment.format('YYYY');
                lastYear = endMoment.format('YYYY');
                loadDaysBetween(startMoment.format('YYYY-MM-DD'), endMoment.format('YYYY-MM-DD'));

                loadHolidaysByYear(startMoment.format('YYYY'));
                if (endMoment.format('YYYY') != startMoment.format('YYYY')) {
                    loadHolidaysByYear(endMoment.format('YYYY'));
                }
            }
            if (view.start.isBefore(startMoment)) {
                loadDaysBetween(view.start.format('YYYY-MM-DD'), startMoment.format('YYYY-MM-DD'));

                if (view.start.format('YYYY') < firstYear) {
                    firstYear = view.start.format('YYYY');
                    loadHolidaysByYear(firstYear);
                }

                startMoment = view.start;
            }
            if (view.end.isAfter(endMoment)) {
                loadDaysBetween(endMoment.format('YYYY-MM-DD'), view.end.format('YYYY-MM-DD'));

                if (view.end.format('YYYY') > lastYear) {
                    lastYear = view.end.format('YYYY');
                    loadHolidaysByYear(lastYear);
                }

                endMoment = view.end;
            }
            if (isChangedLang) {
                isChangedLang = false;
                $scope.uiConfig.calendar.defaultDate = calendarCurrDate;
                $scope.uiConfig.calendar.defaultView = viewType;
            }
        }

        $scope.next = function () {
            $calendar.fullCalendar('next');
        };

        $scope.prev = function () {
            $calendar.fullCalendar('prev');
        };

        $scope.today = function () {
            $calendar.fullCalendar('today');
        };

        function loadAllDays() {
            userService.getDaysByUserId({
                    "userId": self.userId
                },
                function (response) {
                    pushDaysIntoCalendarEventsSource(response);
                },
                function (response) {
                    $scope.presenceDays = [];
                }
            );
        }

        function loadDaysBetween(start, end) {
            if (start === end) {
                userService.getDaysByUserIdAndDate({
                        "userId": self.userId,
                        "date": start
                    },
                    function (response) {
                        var days = [response];
                        pushDaysIntoCalendarEventsSource(days);
                    },
                    function (response) {
                        $scope.presenceDays = [];
                    }
                );
            } else {
                userService.getDaysByUserIdBetweenDates({
                        "userId": self.userId,
                        "start": start,
                        "end": end
                    },
                    function (response) {
                        pushDaysIntoCalendarEventsSource(response);
                    },
                    function (response) {
                        $scope.presenceDays = [];
                    }
                );
            }
        }

        function pushDaysIntoCalendarEventsSource(days) {
            self.days = days;
            angular.forEach(self.days, function (day, index) {
                var timeIntervals = day.timeIntervals;
                for (var i = 0; i < timeIntervals.length; i++) {
                    var presenceDay = {};
                    presenceDay.title = "";
                    presenceDay.start = new Date(day.date[0], day.date[1] - 1, day.date[2], day.timeIntervals[i].startTime[0], day.timeIntervals[i].startTime[1]);
                    presenceDay.end = new Date(day.date[0], day.date[1] - 1, day.date[2], day.timeIntervals[i].endTime[0], day.timeIntervals[i].endTime[1]);
                    presenceDay.stick = 'true';
                    $scope.presenceDays.push(presenceDay);
                }
            });
        }

        function loadHolidaysByYear(year) {
            utilitiesService.getHolidaysByYear({
                    "year": year
                },
                function (response) {
                    pushHolidaysIntoCalendarEventsSource(response);
                },
                function (response) {
                    $scope.holidays = [];
                }
            );
        }

        function pushHolidaysIntoCalendarEventsSource(holidays) {
            angular.forEach(holidays, function (holiday, index) {
                var holidayCalendar = {};
                holidayCalendar.title = " ";
                holidayCalendar.start = new Date(holiday[0], holiday[1] - 1, holiday[2]);
                holidayCalendar.end = new Date(holiday[0], holiday[1] - 1, holiday[2]);
                holidayCalendar.stick = 'true';
                holidayCalendar.allDay = 'true';
                holidayCalendar.rendering = 'background';
                holidayCalendar.color = 'red';
                $scope.holidays.push(holidayCalendar);
            });
        }

        var isChangedLang = false;
        var calendarCurrDate;
        var viewType;
        var firstLoaded = true;

        $rootScope.$watch(function () {
            return $translate.use();
        }, function (lang) {
            if (lang === undefined || firstLoaded) {
                firstLoaded = false;
                return;
            }
            viewType = $calendar.fullCalendar('getView').type;
            calendarCurrDate = $calendar.fullCalendar('getDate');
            isChangedLang = true;
            $scope.uiConfig.calendar.locale = lang;
        });
    }
});
