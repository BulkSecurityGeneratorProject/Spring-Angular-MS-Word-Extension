(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('im-documentroute', {
            parent: 'entity',
            url: '/im-documentroute',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'imicloudApp.imDocument.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/im-document/im-documentsroute.html',
                    controller: 'ImDocumentRouteController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('imDocument');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('im-documentroute-detail', {
            parent: 'entity',
            url: '/im-documentroute/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'imicloudApp.imDocument.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/im-document/im-documentroute-detail.html',
                    controller: 'ImDocumentRouteDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('imDocument');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ImDocument', function($stateParams, ImDocument) {
                    return ImDocument.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'im-documentroute',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('im-documentroute-detail.edit', {
            parent: 'im-documentroute-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/im-document/im-documentroute-dialog.html',
                    controller: 'ImDocumentRouteDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ImDocument', function(ImDocument) {
                            return ImDocument.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('im-documentroute.new', {
            parent: 'im-documentroute',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/im-document/im-documentroute-dialog.html',
                    controller: 'ImDocumentRouteDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                language: null,
                                password: null,
                                documentName: null,
                                originalXml: null,
                                secret: null,
                                defaultTemplate: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('im-documentroute', null, { reload: 'im-documentroute' });
                }, function() {
                    $state.go('im-documentroute');
                });
            }]
        })
        .state('im-documentroute.edit', {
            parent: 'im-documentroute',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/im-document/im-documentroute-dialog.html',
                    controller: 'ImDocumentRouteDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ImDocument', function(ImDocument) {
                            return ImDocument.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('im-documentroute', null, { reload: 'im-documentroute' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('im-documentroute.delete', {
            parent: 'im-documentroute',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/im-document/im-documentroute-delete-dialog.html',
                    controller: 'ImDocumentRouteDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['ImDocument', function(ImDocument) {
                            return ImDocument.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('im-documentroute', null, { reload: 'im-documentroute' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
