var app = angular.module('mainApp');

app.config(['$translateProvider', function ($translateProvider) {
    $translateProvider
        .useStaticFilesLoader({
            prefix: '/js/translations-cookieInfo/',
            suffix: '.json'
        })
        .preferredLanguage('en')
        .fallbackLanguage('en')
        .useCookieStorage() // use cookie storage to remember language app
        //.useLocalStorage() // use local storage to remember language app
       .useSanitizeValueStrategy(null);
}]);