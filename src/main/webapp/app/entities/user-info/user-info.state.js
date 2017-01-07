(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('user-info', {
            parent: 'entity',
            url: '/user-info?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'imicloudApp.userInfo.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/user-info/user-infos.html',
                    controller: 'UserInfoController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('userInfo');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('user-info-detail', {
            parent: 'entity',
            url: '/user-info/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'imicloudApp.userInfo.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/user-info/user-info-detail.html',
                    controller: 'UserInfoDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('userInfo');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'UserInfo', function($stateParams, UserInfo) {
                    return UserInfo.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'user-info',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('user-info-detail.edit', {
            parent: 'user-info-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/user-info/user-info-dialog.html',
                    controller: 'UserInfoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['UserInfo', function(UserInfo) {
                            return UserInfo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('user-info.new', {
            parent: 'user-info',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/user-info/user-info-dialog.html',
                    controller: 'UserInfoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('user-info', null, { reload: 'user-info' });
                }, function() {
                    $state.go('user-info');
                });
            }]
        })
        .state('user-info.edit', {
            parent: 'user-info',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/user-info/user-info-dialog.html',
                    controller: 'UserInfoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['UserInfo', function(UserInfo) {
                            return UserInfo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('user-info', null, { reload: 'user-info' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('user-info.delete', {
            parent: 'user-info',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/user-info/user-info-delete-dialog.html',
                    controller: 'UserInfoDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['UserInfo', function(UserInfo) {
                            return UserInfo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('user-info', null, { reload: 'user-info' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
