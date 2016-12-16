(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('folderroute', {
            parent: 'entity',
            url: '/folderroute',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'imicloudApp.folder.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/folder/foldersroute.html',
                    controller: 'FolderRouteController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('folder');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('folderroute-detail', {
            parent: 'entity',
            url: '/folderroute/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'imicloudApp.folder.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/folder/folderroute-detail.html',
                    controller: 'FolderRouteDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('folder');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Folder', function($stateParams, Folder) {
                    return Folder.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'folderroute',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('folderroute-detail.edit', {
            parent: 'folderroute-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/folder/folderroute-dialog.html',
                    controller: 'FolderRouteDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Folder', function(Folder) {
                            return Folder.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('folderroute.new', {
            parent: 'folderroute',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/folder/folderroute-dialog.html',
                    controller: 'FolderRouteDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                createdAt: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('folderroute', null, { reload: 'folderroute' });
                }, function() {
                    $state.go('folderroute');
                });
            }]
        })
        .state('folderroute.edit', {
            parent: 'folderroute',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/folder/folderroute-dialog.html',
                    controller: 'FolderRouteDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Folder', function(Folder) {
                            return Folder.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('folderroute', null, { reload: 'folderroute' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('folderroute.delete', {
            parent: 'folderroute',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/folder/folderroute-delete-dialog.html',
                    controller: 'FolderRouteDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Folder', function(Folder) {
                            return Folder.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('folderroute', null, { reload: 'folderroute' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
