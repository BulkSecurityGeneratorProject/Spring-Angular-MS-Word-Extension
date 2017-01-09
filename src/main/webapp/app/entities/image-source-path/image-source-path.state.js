(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('image-source-path', {
            parent: 'entity',
            url: '/image-source-path?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'imicloudApp.imageSourcePath.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/image-source-path/image-source-paths.html',
                    controller: 'ImageSourcePathController',
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
                    $translatePartialLoader.addPart('imageSourcePath');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('image-source-path-detail', {
            parent: 'entity',
            url: '/image-source-path/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'imicloudApp.imageSourcePath.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/image-source-path/image-source-path-detail.html',
                    controller: 'ImageSourcePathDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('imageSourcePath');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ImageSourcePath', function($stateParams, ImageSourcePath) {
                    return ImageSourcePath.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'image-source-path',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('image-source-path-detail.edit', {
            parent: 'image-source-path-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/image-source-path/image-source-path-dialog.html',
                    controller: 'ImageSourcePathDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ImageSourcePath', function(ImageSourcePath) {
                            return ImageSourcePath.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('image-source-path.new', {
            parent: 'image-source-path',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/image-source-path/image-source-path-dialog.html',
                    controller: 'ImageSourcePathDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                source: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('image-source-path', null, { reload: 'image-source-path' });
                }, function() {
                    $state.go('image-source-path');
                });
            }]
        })
        .state('image-source-path.edit', {
            parent: 'image-source-path',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/image-source-path/image-source-path-dialog.html',
                    controller: 'ImageSourcePathDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ImageSourcePath', function(ImageSourcePath) {
                            return ImageSourcePath.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('image-source-path', null, { reload: 'image-source-path' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('image-source-path.delete', {
            parent: 'image-source-path',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/image-source-path/image-source-path-delete-dialog.html',
                    controller: 'ImageSourcePathDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['ImageSourcePath', function(ImageSourcePath) {
                            return ImageSourcePath.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('image-source-path', null, { reload: 'image-source-path' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
