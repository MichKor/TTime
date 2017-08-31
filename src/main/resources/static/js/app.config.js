"use strict";
angular.module('mainApp')
    .config(function ($stateProvider, $urlServiceProvider, $httpProvider, $breadcrumbProvider) {
        $breadcrumbProvider.setOptions({
            templateUrl: 'js/breadcrumb.template.html'
        });

        $stateProvider
            .state('mainApp', {
                url: "/",
                abstract: true,
                templateUrl: "index.html"
            })
            .state('login', {
                url: "/login",
                component: 'login',
                autoRedirection: {
                    isLoggedIn: 'dashboard'
                }
            })
            .state('home', {
                url: "/home",
                component: 'home',
                resolve: {
                    authenticationPromise: function ($rootScope) {
                        return $rootScope.authenticationPromise;
                    },

                    autoRedirection: function () {
                        return {
                            isLoggedIn: 'dashboard'
                        };
                    }
                }
            })
            .state('dashboard', {
                url: "/dashboard",
                component: 'dashboard',
                redirectTo: 'dashboard.teamSchedule',
                ncyBreadcrumb: {
                    label: 'TTimeManager'
                },
                autoRedirection: {
                    isNotLoggedIn: 'login'
                }
            })
            .state('dashboard.schedule', {
                url: "/schedule",
                component: 'schedule',
                params: {
                    "userId": null,
                    "userName": null
                },
                ncyBreadcrumb: {
                    label: '{{breadcrumbMessage? breadcrumbMessage : "MY_SCHEDULE" | translate}} {{userName}}'
                },
                autoRedirection: {
                    isNotLoggedIn: 'login'
                }
            })
            .state('dashboard.editSchedule', {
                url: "/edit-schedule",
                component: 'dashboardEditSchedule',
                params: {
                    currentDate: null
                },
                ncyBreadcrumb: {
                    label: '{{"EDITING" | translate}}',
                    parent: 'dashboard.schedule'
                },
                autoRedirection: {
                    isNotLoggedIn: 'login'
                }
            })
            .state('dashboard.templates', {
                url: "/templates",
                component: 'templates',
                ncyBreadcrumb: {
                    label: '{{"TEMPLATES" | translate}}'
                },
                autoRedirection: {
                    isNotLoggedIn: 'login'
                }
            })
            .state('dashboard.teams', {
                url: "/teams",
                component: 'teams',
                ncyBreadcrumb: {
                    label: '{{"TEAMS" | translate}}'
                },
                autoRedirection: {
                    isNotLoggedIn: 'login'
                }
            })
            .state('dashboard.profile-view', {
                url: "/profile-view",
                component: 'profileView',
                ncyBreadcrumb: {
                    label: '{{"MY_PROFILE" | translate}}'
                },
                autoRedirection: {
                    isNotLoggedIn: 'login'
                }
            })
            .state('dashboard.profile-edit', {
                url: "/profile-edit",
                component: 'profileEdit',
                ncyBreadcrumb: {
                    parent: 'dashboard.profile-view',
                    label: '{{"EDITING" | translate}}'
                },
                autoRedirection: {
                    isNotLoggedIn: 'login'
                }
            })
            .state('dashboard.teamSchedule', {
                url: "/teamSchedule",
                component: 'teamSchedule',
                params: {
                    "teamId": null,
                    "teamName": null
                },
                ncyBreadcrumb: {
                    label: '{{"SCHEDULE_FOR_TEAM" | translate}} {{breadcrumbMessage}}'
                },
                autoRedirection: {
                    isNotLoggedIn: 'login'
                }
            })
            .state('dashboard.reports',{
                url: '/reports',
                component: 'reports',
                ncyBreadcrumb: {
                    label: '{{"REPORTS" | translate}}'
                },
                autoRedirection: {
                    isNotLoggedIn: 'login',
                    jiraReportsDisabled: 'dashboard.teamSchedule'    
                }
            });

        $urlServiceProvider.rules.otherwise({
            state: 'home'
        });
        $httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';
        $httpProvider.interceptors.push('loaderInterceptor');
        $httpProvider.interceptors.push('HttpResponse');
    });