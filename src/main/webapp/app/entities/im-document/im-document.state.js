(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('im-document', {
            parent: 'entity',
            url: '/im-document',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'imicloudApp.imDocument.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/im-document/im-documents.html',
                    controller: 'ImDocumentController',
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
        .state('im-document-detail', {
            parent: 'entity',
            url: '/im-document/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'imicloudApp.imDocument.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/im-document/im-document-detail.html',
                    controller: 'ImDocumentDetailController',
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
                        name: $state.current.name || 'im-document',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('im-document-detail.edit', {
            parent: 'im-document-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/im-document/im-document-dialog.html',
                    controller: 'ImDocumentDialogController',
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
        .state('im-document.new', {
            parent: 'im-document',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/im-document/im-document-dialog.html',
                    controller: 'ImDocumentDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                language: null,
                                password: null,
                                tempPassword: null,
                                documentName: null,
                                originalXml: null,
                                tempXml: null,
                                secret: null,
                                defaultTemplate: null,
                                tempTemplate: null,
                                uploadComplete: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('im-document', null, { reload: 'im-document' });
                }, function() {
                    $state.go('im-document');
                });
            }]
        })
        .state('im-document.edit', {
            parent: 'im-document',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/im-document/im-document-dialog.html',
                    controller: 'ImDocumentDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ImDocument', function(ImDocument) {
                            return ImDocument.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('im-document', null, { reload: 'im-document' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('im-document.delete', {
            parent: 'im-document',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/im-document/im-document-delete-dialog.html',
                    controller: 'ImDocumentDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['ImDocument', function(ImDocument) {
                            return ImDocument.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('im-document', null, { reload: 'im-document' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
