(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('im-blockroute', {
            parent: 'entity',
            url: '/im-blockroute',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'imicloudApp.imBlock.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/im-block/im-blocksroute.html',
                    controller: 'ImBlockRouteController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('imBlock');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('im-blockroute-detail', {
            parent: 'entity',
            url: '/im-blockroute/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'imicloudApp.imBlock.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/im-block/im-blockroute-detail.html',
                    controller: 'ImBlockRouteDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('imBlock');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ImBlock', function($stateParams, ImBlock) {
                    return ImBlock.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'im-blockroute',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('im-blockroute-detail.edit', {
            parent: 'im-blockroute-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/im-block/im-blockroute-dialog.html',
                    controller: 'ImBlockRouteDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ImBlock', function(ImBlock) {
                            return ImBlock.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('im-blockroute.new', {
            parent: 'im-blockroute',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/im-block/im-blockroute-dialog.html',
                    controller: 'ImBlockRouteDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                label: null,
                                content: null,
                                position: null,
                                guid: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('im-blockroute', null, { reload: 'im-blockroute' });
                }, function() {
                    $state.go('im-blockroute');
                });
            }]
        })
        .state('im-blockroute.edit', {
            parent: 'im-blockroute',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/im-block/im-blockroute-dialog.html',
                    controller: 'ImBlockRouteDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ImBlock', function(ImBlock) {
                            return ImBlock.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('im-blockroute', null, { reload: 'im-blockroute' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('im-blockroute.delete', {
            parent: 'im-blockroute',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/im-block/im-blockroute-delete-dialog.html',
                    controller: 'ImBlockRouteDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['ImBlock', function(ImBlock) {
                            return ImBlock.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('im-blockroute', null, { reload: 'im-blockroute' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
