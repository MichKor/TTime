angular.module('mainApp')
    .directive('templateForDay', function() {
        return {
            restrict : 'E',
            templateUrl : 'js/directives/template-for-day.html',
            scope : {
                templates: '=',
                returnCallback: '=',
                day: '@'
            },
            link : function(scope, element, attrs) {
                scope.sendTemplate = function(template){
                    scope.returnCallback(template);
                }

                scope.hasDayInTemplate = function(template){
                    console.log(template)
                    return true;
                }
                scope.fillWithTimeIntervals = function (template) {
                    if(template.dayWeeks.length === 0) return 'Nieobecny';
                    var tooltip = '';
                    angular.forEach(template.dayWeeks[0].timeIntervals, function (timeInterval) {
                        tooltip += fixHours(timeInterval.startTime) + '-';
                        tooltip += fixHours(timeInterval.endTime);
                        tooltip += '\n'
                    })
                    return tooltip
                }



                function fixHours(time){
                    return ("0" + time[0]).slice(-2) + ':' +("0" + time[1]).slice(-2);
                }

            }
        }
    });