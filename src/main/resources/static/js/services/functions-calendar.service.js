angular.module('mainApp').factory('calendarService', function ($rootScope, userService, timeUtilities) {
    var calendarFunctions = {};
    calendarFunctions.days = [];
    calendarFunctions.defaultTemplate = {};
    calendarFunctions.templates = {};

    var eventData = {
        allDay: false,
        stick: true,
        color: '#24dff0'
    }

    calendarFunctions.addEvent = function (start, end, collection) {
        if (!calendarFunctions.isPastDay(start._d)) {
            interval = calendarFunctions.fixAllDay(start, end);
            start = interval[0];
            end = interval[1];
            if(end-start < 3600000) return;
            calendarFunctions.removeExistEvent(start, end, collection);
            var newEventData = angular.copy(eventData);
            newEventData.start = new Date(start._d);
            newEventData.end = new Date(end._d);
            collection.push(newEventData);
            return newEventData;
        }
        return undefined;
    }

    calendarFunctions.isPastDay = function (day) {
        return !moment(day).isSameOrAfter($rootScope.serverTime,'day');
    };

    //Lub zmienić na domyślny szablon
    calendarFunctions.fixAllDay = function (start, end) {
        if (end._d.getDate() - start._d.getDate() === 1) {
            end._d.setDate(end._d.getDate() - 1);
            end._d.setHours(16);
            start._d.setHours(8);
        }
        return [start, end];
    }

    calendarFunctions.add8Hours = function (start, end) {
        var timeStart = start._d;
        var timeEnd = end._d;
        var difference = timeEnd.getHours() - timeStart.getHours();
        if (difference < 1 || (difference < 2 && timeEnd.getMinutes() === 0 && timeStart.getMinutes() === 30)) {
            var addHours = start._d.getHours() + 8 <= 22 ? 8 : 22 - start._d.getHours();
            if (start._d.getMinutes() === 0)
                end._d.setMinutes(0);
            else {
                end._d.setMinutes(30);
                addHours--;
            }
            end._d.setHours(end._d.getHours() + addHours);
        }
        return end;
    };

    calendarFunctions.removeExistEvent = function (start, end, collection) {
        var day = new Date(start._d.getFullYear(), start._d.getMonth(), start._d.getDate());
        var timeInterval = startEndToTimeInterval(start._d, end._d);
        var indexes = [];
        for (var i = 0; i < collection.length; i++) {
            var begin = collection[i].start._d;
            if (begin === undefined) begin = collection[i].start;
            if (!calendarFunctions.isSameDay(day, begin)) continue;
            if (isOverlapping(timeInterval, calendarFunctions.eventToTimeInterval(collection[i])))
                indexes.push(i);
        }
        indexes = Array.from(new Set(indexes));
        for (var j = indexes.length - 1; j >= 0; j--)
            collection.splice(indexes[j], 1);
        return collection;
    };

    function startEndToTimeInterval(start, end) {
        return {
            startTime: [start.getHours(), start.getMinutes()],
            endTime: [end.getHours(), end.getMinutes()]
        };
    };

    calendarFunctions.isSameDay = function (day, day2) {
        var d = moment(day), d2 = moment(day2);
        return d.dayOfYear() === d2.dayOfYear() && d.year() === d2.year();
    }

    calendarFunctions.isOverlappingCollection = function (collection, timeInterval) {
        var result = false;
        angular.forEach(collection, function (col) {
            if (isOverlapping(col, timeInterval)) {
                result = true;
                return;
            }
        });
        return result;
    }

    function isOverlapping(timeInterval, timeInterval2) {
        var a = timeInterval
        var b = timeInterval2
        if (timeUtilities.compareTimes(a.startTime, b.startTime) > 0) {
            var tmp = a;
            a = b;
            b = tmp;
        }
        return timeUtilities.compareTimes(b.startTime, a.endTime) <= 0;
    }

    calendarFunctions.eventToTimeInterval = function (event) {
        var s = event.start._d,
            e = event.end._d;
        if (s === undefined) {
            s = event.start;
            e = event.end;
        }
        return {
            startTime: [s.getHours(), s.getMinutes()],
            endTime: [e.getHours(), e.getMinutes()]
        };
    }

    function eventToDay(event) {
        var date = event.start._d;
        if (date === undefined) date = event.start;
        return [date.getFullYear(), date.getMonth(), date.getDate()];
    }

    calendarFunctions.eventsToDaysAndIntervals = function (events) {
        var days = {};
        angular.forEach(events, function (e) {
            var tmp = e.start._d;
            if (tmp === undefined) tmp = e.start;
            days[eventToDay(e)] = {
                date: [tmp.getFullYear(), tmp.getMonth(), tmp.getDate()],
                timeIntervals: []
            };
        });
        angular.forEach(events, function (e) {
            days[eventToDay(e)].timeIntervals.push(calendarFunctions.eventToTimeInterval(e));
        });
        return days;
    }


    calendarFunctions.responseToEvents = function (response) {
        var result = [];
        angular.forEach(response, function (day) {
            var timeIntervals = day.timeIntervals;
            for (var i = 0; i < timeIntervals.length; i++) {
                var presenceDay = {};
                presenceDay.title = "";
                presenceDay.start = new Date(day.date[0], day.date[1] - 1, day.date[2], day.timeIntervals[i].startTime[0], day.timeIntervals[i].startTime[1]);
                presenceDay.end = new Date(day.date[0], day.date[1] - 1, day.date[2], day.timeIntervals[i].endTime[0], day.timeIntervals[i].endTime[1]);
                presenceDay.stick = true;
                result.push(presenceDay);
            }
        });
        return result;
    };


    var days = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];
    calendarFunctions.getDayOfWeek = function (day) {
        return days.indexOf(day);
    }

    return calendarFunctions;


});
