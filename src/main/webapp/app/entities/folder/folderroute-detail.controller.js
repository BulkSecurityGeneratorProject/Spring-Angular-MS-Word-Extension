(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .controller('FolderRouteDetailController', FolderRouteDetailController);

    FolderRouteDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Folder', 'Organization', 'ImDocument'];

    function FolderRouteDetailController($scope, $rootScope, $stateParams, previousState, entity, Folder, Organization, ImDocument) {
        var vm = this;

        vm.folder = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('imicloudApp:folderUpdate', function(event, result) {
            vm.folder = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
