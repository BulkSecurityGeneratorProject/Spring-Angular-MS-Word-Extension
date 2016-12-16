(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('imageroute', {
            parent: 'entity',
            url: '/imageroute',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'imicloudApp.image.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/image/imagesroute.html',
                    controller: 'ImageRouteController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('image');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('imageroute-detail', {
            parent: 'entity',
            url: '/imageroute/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'imicloudApp.image.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/image/imageroute-detail.html',
                    controller: 'ImageRouteDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('image');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Image', function($stateParams, Image) {
                    return Image.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'imageroute',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('imageroute-detail.edit', {
            parent: 'imageroute-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/image/imageroute-dialog.html',
                    controller: 'ImageRouteDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Image', function(Image) {
                            return Image.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('imageroute.new', {
            parent: 'imageroute',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/image/imageroute-dialog.html',
                    controller: 'ImageRouteDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                filename: null,
                                createdAt: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('imageroute', null, { reload: 'imageroute' });
                }, function() {
                    $state.go('imageroute');
                });
            }]
        })
        .state('imageroute.edit', {
            parent: 'imageroute',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/image/imageroute-dialog.html',
                    controller: 'ImageRouteDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Image', function(Image) {
                            return Image.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('imageroute', null, { reload: 'imageroute' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('imageroute.delete', {
            parent: 'imageroute',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/image/imageroute-delete-dialog.html',
                    controller: 'ImageRouteDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Image', function(Image) {
                            return Image.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('imageroute', null, { reload: 'imageroute' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
