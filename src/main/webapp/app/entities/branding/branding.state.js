(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('branding', {
            parent: 'entity',
            url: '/branding',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'imicloudApp.branding.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/branding/brandings.html',
                    controller: 'BrandingController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('branding');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('branding-detail', {
            parent: 'entity',
            url: '/branding/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'imicloudApp.branding.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/branding/branding-detail.html',
                    controller: 'BrandingDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('branding');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Branding', function($stateParams, Branding) {
                    return Branding.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'branding',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('branding-detail.edit', {
            parent: 'branding-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/branding/branding-dialog.html',
                    controller: 'BrandingDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Branding', function(Branding) {
                            return Branding.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('branding.new', {
            parent: 'branding',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/branding/branding-dialog.html',
                    controller: 'BrandingDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                primaryColor: null,
                                secundaryColor: null,
                                pageBackgroundColor: null,
                                textColor: null,
                                name: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('branding', null, { reload: 'branding' });
                }, function() {
                    $state.go('branding');
                });
            }]
        })
        .state('branding.edit', {
            parent: 'branding',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/branding/branding-dialog.html',
                    controller: 'BrandingDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Branding', function(Branding) {
                            return Branding.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('branding', null, { reload: 'branding' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('branding.delete', {
            parent: 'branding',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/branding/branding-delete-dialog.html',
                    controller: 'BrandingDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Branding', function(Branding) {
                            return Branding.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('branding', null, { reload: 'branding' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
