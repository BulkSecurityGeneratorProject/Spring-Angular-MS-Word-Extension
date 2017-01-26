(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .controller('ImDocumentDetailController', ImDocumentDetailController);

    ImDocumentDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'DataUtils', 'entity', 'ImDocument', 'Folder', 'ImMap', 'User', 'Branding'];

    function ImDocumentDetailController($scope, $rootScope, $stateParams, previousState, DataUtils, entity, ImDocument, Folder, ImMap, User, Branding) {
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
