// 'use strict';
angular.module('mainApp')
    .component('dashboardEditSchedule', {
        templateUrl: 'js/dashboard/edit-schedule/edit-schedule.template.html',
        controller:
            function ($state, $scope, $timeout, $compile, $rootScope, $stateParams, dayService, userService, calendarService, scheduleService, utilitiesService, $uibModal, $translate, $q) {

                var self = this;
                var editableWorkHours = {};

                var firstDate, lastDate;
                var firstYear, lastYear;

                $scope.events = [];
                $scope.holidays = [];
                //TODO gdy holidays jest dodane jako oddzielny source do eventsources to sa bledy przy usuwaniu eventow
                $scope.eventSources = [$scope.events];

                var defaultColor = '#4718a7',
                    absenceColor = '#a3121b';

                var openOfficeHours = moment.duration(14,'hour').as('milliseconds');//6:00-20:00
                var twentyFourHours = 86400000;
                var hourTimeZone = Math.abs(new Date().getTimezoneOffset()) / 60;

                self.templates = [];
                self.defaultTemplate = {};

                $scope.modalShown = false;

                $calendar = $('[ui-calendar]');

                self.showButton = false;

                var callbacks = [];

                $scope.uiConfig = {
                    calendar: {
                        defaultDate: $stateParams.currentDate?$stateParams.currentDate:moment(),
                        locale: $translate.use(),
                        lang: $translate.use(),
                        allDaySlot: true,
                        eventColor: defaultColor,
                        editable: false,
                        displayEventEnd: 'true',
                        header: {
                            left: 'month,agendaWeek',
                            center: 'title',
                            right: 'today prev,next'
                        },
                        firstDay: '1',
                        businessHours: {
                            dow: [1, 2, 3, 4, 5],
                            start: '6:00',
                            end: '20:00'
                        },
                        hiddenDays: [0, 6],
                        maxTime: "20:00:00",
                        minTime: "06:00:00",
                        slotDuration: '00:15:00',
                        timeFormat: 'H(:mm)',
                        timezone: 'local',
                        now: $rootScope.serverTime,
                        selectable: true,
                        selectHelper: true,
                        weekNumberTitle: $translate.instant('WEEK'),
                        weekNumberCalculation: 'ISO',
                        weekNumbers: true,
                        navLinks: true,
                        select: selectedTimeInterval,
                        navLinkWeekClick: openTemplatesMenu,
                        navLinkDayClick: prepareDateToShowModalDay,
                        eventRender: fillEvent,
                        viewRender: viewRender,
                        dayClick: dayClick,
                        eventClick: eventClick
                    }
                };

                function viewRender(view) {
                    
                    $scope.actualView = view.type;
                    
                    if ($scope.actualView === 'agendaWeek') {
                        var weekStart = alignToMonday($calendar.fullCalendar('getDate'));
                        self.showButton = weekStart.isSameOrAfter($rootScope.serverTime, 'week');
                    }
                    if (firstDate === undefined) {
                        firstDate = moment(view.start);
                        lastDate = moment(view.end);
                        
                        firstYear = firstDate.format('YYYY');
                        lastYear = lastDate.format('YYYY');
                        loadHolidaysByYear(firstDate.format('YYYY'));
                        if(lastDate.format('YYYY') != firstDate.format('YYYY')){
                            loadHolidaysByYear(lastDate.format('YYYY'));
                        }
                        
                        getEventsBetweenDays(firstDate.format('YYYY-MM-DD'), lastDate.format('YYYY-MM-DD'));
                    }
                    if (view.start.isBefore(firstDate)) {
                        getEventsBetweenDays(view.start.format('YYYY-MM-DD'),firstDate.format('YYYY-MM-DD'));
                        
                        if (view.start.format('YYYY') < firstYear) {
                            firstYear = view.start.format('YYYY');
                            loadHolidaysByYear(firstYear);
                        }
                        
                        firstDate = view.start;
                    }
                    if (view.end.isAfter(lastDate)) {
                        getEventsBetweenDays(lastDate.format('YYYY-MM-DD'), view.end.format('YYYY-MM-DD'));
                        
                        if (view.end.format('YYYY') > lastYear) {
                            lastYear = view.end.format('YYYY');
                            loadHolidaysByYear(lastYear);
                        }
                        
                        lastDate = view.end;
                    }
                    if (isChangedLang) {
                        isChangedLang = !isChangedLang;
                        $scope.uiConfig.calendar.defaultDate = currDate;
                        $scope.uiConfig.calendar.defaultView = viewType;
                    }
                }

                function getEventsBetweenDays(start, end) {
                    userService.getDaysByUserIdBetweenDates({
                            "userId": $rootScope.currentUser.id,
                            "start": start,
                            "end": end
                        },
                        function (response) {
                            pushDaysIntoCalendarEventsSource(response);
                        },
                        function (response) {
                            if (response.status === 204) {
                            }
                        }
                    );
                }

                function pushDaysIntoCalendarEventsSource(collection) {
                    $scope.events.push.apply($scope.events, calendarService.responseToEvents(collection));
                    angular.forEach(collection, function (r) {
                        if (r.timeIntervals.length === 0) {
                            var startDate = new Date(r.date[0], r.date[1] - 1, r.date[2], 6, 0);
                            var endDate = new Date(r.date[0], r.date[1] - 1, r.date[2], 20, 0);
                            if(!checkIfHoliday(moment(startDate))){
                                addAbsentEvent(startDate, endDate);
                            }                        
                        }
                    })
                }
                
                function checkIfHoliday(date){
                    for(var i = 0; i < $scope.holidays.length; i++){
                        if(moment($scope.holidays[i].start).format('YYYY-MM-DD') === copyTime(date).format('YYYY-MM-DD')){
                            return true;
                        }
                    }
                    return false;
                }


                function dayClick(date, jsEvent, view) {
                    if (view.type === 'agendaWeek') return;
                    prepareDateToShowModalDay(date);
                }

                function prepareDateToShowModalDay(date) {
                    if(!checkIfHoliday(date)){
                        var start = copyTime(date).hour(8).minutes(0);
                        var end = moment(start).add(8, 'h');
                        showModalDay(start, end);
                    }else{
                        return;
                    }               
                }

                function eventClick(event, jsEvent) {
                    if (isClickedDelete(jsEvent)) return;
                    var start = copyTime(event.start);
                    var end = event.end;
                    if (event.allDay) {
                        start.hour(6);
                        end = moment(start).hour(20);
                    }
                    showModalDay(start, end);
                }

                function isClickedDelete(jsEvent) {
                    return angular.element(jsEvent.target).hasClass('closeon');
                }

                function copyTime(dateTime) {
                    return moment().set({
                        year: dateTime.year(),
                        month: dateTime.month(),
                        date: dateTime.date(),
                        hour: dateTime.hour(),
                        minute: dateTime.minute(),
                        seconds: 0
                    });
                }

                function showModalDay(start, end) {
                    if (calendarService.isPastDay(start)) return;
                    var modalInstance = $uibModal.open({
                        animation: true,
                        component: 'editHoursModal',
                        resolve: {
                            start: function () {
                                return start;
                            },
                            end: function () {
                                return end;
                            },
                            templates: function () {
                                return self.templates;
                            }
                        }
                    });

                    modalInstance.result.then(function (response) {
                        if (response.template !== undefined) {
                            addDayFromChooseTemplate(response.date, response.template)
                        }
                        else if (response.date !== undefined) {
                            addAbsentAllDay(response.date);
                        }
                        else {
                            selectedTimeInterval(response.start, response.end)
                        }
                    }, function () {
                    })
                }

                self.resetCalendar = function () {
                    $state.reload();
                };

                var isChangedLang = false;
                var currDate;
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
                    currDate = $calendar.fullCalendar('getDate');
                    isChangedLang = true;
                    $scope.uiConfig.calendar.weekNumberTitle = $translate.instant('WEEK');
                    angular.forEach($scope.events, function (e) {
                        if (e.allDay) e.title = $translate.instant('ABSENT');
                    });
                    $scope.uiConfig.calendar.locale = lang;
                });
                self.saveCalendar = function () {
                    var allDaysAndIntervals = calendarService.eventsToDaysAndIntervals($scope.events);
                    var intersection = [];
                    angular.forEach(editableWorkHours, function (e, key) {
                        if (e.timeIntervals === undefined || e.timeIntervals.length === 0) {
                            //2017,8,2 -> [2017,8,2]
                            var date = key.split(',').map(function (item) {
                                return parseInt(item, 10);
                            });
                            intersection.push({date: date});
                        }
                        else if (allDaysAndIntervals[key] !== undefined)
                            intersection.push(allDaysAndIntervals[key])

                    });
                    angular.forEach(intersection, function (e) {
                        e.date[1]++;
                    });
                    dayService.saveDays(intersection, function () {
                        $state.go('home')
                    });
                };

                function selectedTimeInterval(start, end) {
                    
                    var sumSelectedTimeInterval = end._d - start._d;
                    if (checkIfHoliday(start) || isClickedDayFromMonthView(start, sumSelectedTimeInterval)) {
                        $calendar.fullCalendar('unselect');
                        return;
                    }
                    if (isSelectedOneDay(start, end, sumSelectedTimeInterval)) {
                        var eventData = calendarService.addEvent(start, end, $scope.events);
                        if (eventData !== undefined) {
                            addDayHours(start._d, eventData)
                        }
                    }
                    $calendar.fullCalendar('unselect');
                }

                function addDayFromChooseTemplate(selectedDay, template) {
                    selectedDay.locale('en');
                    var currentSelectDay = moment(selectedDay).format('dddd').toUpperCase();
                    if (self.defaultTemplate === undefined) return;
                    if (template.dayWeeks.length === 0) {
                        var start = moment(selectedDay).hour(6).minutes(0);
                        var end = moment(start).hour(20);
                        calendarService.removeExistEvent(start, end, $scope.events);
                        addAbsentEvent(new Date(start), new Date(end));
                        return;
                    }
                    angular.forEach(template.dayWeeks, function (d) {
                        if (d.day === currentSelectDay) {
                            var s = moment(selectedDay)
                            var e = moment(selectedDay)
                            angular.forEach(d.timeIntervals, function (timeInterval) {
                                setHoursMinutes(s, timeInterval.startTime);
                                setHoursMinutes(e, timeInterval.endTime);
                                addDayHours(s._d, calendarService.addEvent(s, e, $scope.events));
                            });
                        }
                    })
                }

                function isClickedDayFromMonthView(start, selectedInterval) {
                    return start._d.getHours() === hourTimeZone && twentyFourHours - selectedInterval === 0;
                }

                function isSelectedOneDay(start, end, selectedInterval) {
                    return start._d.getDate() === end._d.getDate() && (openOfficeHours - selectedInterval) > 0;
                }

                function addDayHours(day, eventData) {
                    var fullDate = [day.getFullYear(), day.getMonth(), day.getDate()];
                    if (editableWorkHours[fullDate] === undefined
                        || calendarService.isOverlappingCollection(editableWorkHours[fullDate].timeIntervals, calendarService.eventToTimeInterval(eventData))) {
                        editableWorkHours[fullDate] = {
                            timeIntervals: [calendarService.eventToTimeInterval(eventData)]
                        }
                    }
                    else {
                        editableWorkHours[fullDate].timeIntervals.push(calendarService.eventToTimeInterval(eventData))
                    }
                }

                function setHoursMinutes(time, time2) {
                    time.set({
                        'hour': time2[0],
                        'minute': time2[1]
                    })
                }

                var modalViewTemplatesMenu = function () {
                    return $uibModal.open({
                        animation: true,
                        component: 'templateChoiceModal',
                        resolve: {
                            templates: function () {
                                return self.templates;
                            }
                        }
                    });
                };

                self.openTemplateChoiceModal = function () {
                    var weekStart = alignToMonday($calendar.fullCalendar('getDate'));
                    openTemplatesMenu(weekStart);
                };

                function alignToMonday(event) {
                    var day = event._d;
                    day.setDate(day.getDate() - (day.getDay() - 1));
                    event._d = day;
                    return event;
                }

                function openTemplatesMenu(weekStart) {
                    if (!weekStart.isSameOrAfter($rootScope.serverTime, 'week')) return;
                    var modalInstance = modalViewTemplatesMenu();
                    modalInstance.result.then(function (confirm) {
                        fillWeekTemplate(confirm, weekStart)
                    }, function () {

                    })
                }

                function fillWeekTemplate(template, weekStart) {
                    // @formatter:off
                    var days = new Date(weekStart._d);days.setHours(6);days.setMinutes(0);
                    var daye = new Date(weekStart._d);daye.setHours(20);daye.setMinutes(0);
                    // @formatter:on
                    var leftDays = {};
                    leftDays["MONDAY"] = leftDays["TUESDAY"] = leftDays["WEDNESDAY"] = leftDays["THURSDAY"] = leftDays["FRIDAY"] = [];
                    var indexes = [];
                    angular.forEach(leftDays, function (e, k) {
                        var s = moment(days);
                        s.add(calendarService.getDayOfWeek(k), 'days');
                        var date = [s.get('year'), s.get('month'), s.get('date')];
                        if (calendarService.isPastDay(s)) {
                            delete leftDays[k];
                            return;
                        }
                        editableWorkHours[date] = {timeIntervals: []};
                        for (var i = 0; i < $scope.events.length; i++) {
                            var tmp = $scope.events[i].start;
                            var time = moment(new Date(date[0], date[1], date[2]));
                            var time2 = moment(new Date(tmp.getFullYear(), tmp.getMonth(), tmp.getDate()));
                            if(!checkIfHoliday(s)){
                                if (time.isSame(time2, 'day')) indexes.push($scope.events[i]);
                            }
                            
                        }
                        delete leftDays[k];
                    })
                    angular.forEach(indexes, function (e) {
                        $scope.events.splice($scope.events.indexOf(e), 1);
                    })

                    leftDays["MONDAY"] = leftDays["TUESDAY"] = leftDays["WEDNESDAY"] = leftDays["THURSDAY"] = leftDays["FRIDAY"] = [];
                    var perscenceDays = {};
                    angular.forEach(template.dayWeeks, function (dayWeek) {
                        var tmp = calendarService.getDayOfWeek(dayWeek.day);
                        var s = moment(days).add(tmp, 'days');
                        var e = moment(daye).add(tmp, 'days');
                        s.locale('en');
                        perscenceDays[s.format('dddd').toUpperCase()] = [];
                        if (calendarService.isPastDay(s._d)) return;
                        angular.forEach(dayWeek.timeIntervals, function (timeInterval) {
                            setHoursMinutes(s, timeInterval.startTime);
                            setHoursMinutes(e, timeInterval.endTime);
                            selectedTimeInterval(s, e);
                            if(!checkIfHoliday(s)){
                                addDayHours(s._d, calendarService.addEvent(s, e, $scope.events));
                            }            
                        })
                    });
                    angular.forEach(leftDays, function (l, k) {
                        if (perscenceDays[k] !== undefined) return;
                        var st = moment(days).add(calendarService.getDayOfWeek(k), 'days').hour(6);
                        var en = moment(days).add(calendarService.getDayOfWeek(k), 'days').hour(20);
                        if (calendarService.isPastDay(st)) return;
                        if(!checkIfHoliday(st)){
                            calendarService.removeExistEvent(st, en, $scope.events);
                            addAbsentEvent(new Date(st), new Date(en));
                        }           
                    })
                }

                function fillEvent(event, element, view) {
                    scheduleService.holidayRender(event, element);
                    
                    if (!calendarService.isPastDay(event.start._d)) {
                        element.find(".fc-content").prepend("" +
                            "<button type='button' class='btn btn-danger closeon'>X</button>");
                        element.find(".closeon").click(function () {
                            var i = event.source.events.indexOf(event);
                            var start = $scope.events[i].start._d;
                            if (start === undefined) start = $scope.events[i].start;
                            var date = [start.getFullYear(), start.getMonth(), start.getDate()];
                            editableWorkHours[date] = {timeIntervals: []};
                            $scope.events.splice(i, 1);
                        });
                    }
                }

                function addAbsentAllDay(date) {
                    var s = moment(date).hours(6);
                    var e = moment(date).hours(20);
                    calendarService.removeExistEvent(s, e, $scope.events);
                    addAbsentEvent(new Date(s), new Date(e))
                    var d = [s.isoWeekYear(), s.month(), s.date()];
                    editableWorkHours[d] = {timeIntervals: []};
                }

                function addAbsentEvent(start, end) {
                    return $scope.events.push({
                        allDay: true,
                        start: start,
                        end: end,
                        stick: true,
                        title: $translate.instant('ABSENT'),
                        color: absenceColor
                    })
                }

                userService.getTemplates(function (resp) {
                    if (resp !== undefined) {
                        self.templates = resp.templates;
                        self.defaultTemplate = getDefaultTemplate(self.templates, resp.defaultId);
                    }
                });

                function getDefaultTemplate(templates, defId) {
                    var template = undefined;
                    angular.forEach(templates, function (t) {
                        if (t.id === defId) return template = t;
                    });
                    return template;
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
                        $scope.events.push(holidayCalendar);
                    });
                }
                  
            }
    })
;