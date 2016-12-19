(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .controller('ImDocumentRouteDetailController', ImDocumentRouteDetailController);

    ImDocumentRouteDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'DataUtils', 'entity', 'ImDocument', 'Folder', 'ImMap', 'User'];

    function ImDocumentRouteDetailController($scope, $rootScope, $stateParams, previousState, DataUtils, entity, ImDocument, Folder, ImMap, User) {
        var vm = this;

        vm.imDocument = entity;
        vm.previousState = previousState.name;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('imicloudApp:imDocumentUpdate', function(event, result) {
            vm.imDocument = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
