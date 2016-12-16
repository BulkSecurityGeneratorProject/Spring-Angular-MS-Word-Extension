(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('im-maproute', {
            parent: 'entity',
            url: '/im-maproute',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'imicloudApp.imMap.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/im-map/im-mapsroute.html',
                    controller: 'ImMapRouteController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('imMap');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('im-maproute-detail', {
            parent: 'entity',
            url: '/im-maproute/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'imicloudApp.imMap.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/im-map/im-maproute-detail.html',
                    controller: 'ImMapRouteDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('imMap');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ImMap', function($stateParams, ImMap) {
                    return ImMap.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'im-maproute',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('im-maproute-detail.edit', {
            parent: 'im-maproute-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/im-map/im-maproute-dialog.html',
                    controller: 'ImMapRouteDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ImMap', function(ImMap) {
                            return ImMap.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('im-maproute.new', {
            parent: 'im-maproute',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/im-map/im-maproute-dialog.html',
                    controller: 'ImMapRouteDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                guid: null,
                                label: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('im-maproute', null, { reload: 'im-maproute' });
                }, function() {
                    $state.go('im-maproute');
                });
            }]
        })
        .state('im-maproute.edit', {
            parent: 'im-maproute',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/im-map/im-maproute-dialog.html',
                    controller: 'ImMapRouteDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ImMap', function(ImMap) {
                            return ImMap.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('im-maproute', null, { reload: 'im-maproute' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('im-maproute.delete', {
            parent: 'im-maproute',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/im-map/im-maproute-delete-dialog.html',
                    controller: 'ImMapRouteDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['ImMap', function(ImMap) {
                            return ImMap.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('im-maproute', null, { reload: 'im-maproute' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
