(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .controller('ImBlockRouteDetailController', ImBlockRouteDetailController);

    ImBlockRouteDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'DataUtils', 'entity', 'ImBlock', 'ImMap', 'Image'];

    function ImBlockRouteDetailController($scope, $rootScope, $stateParams, previousState, DataUtils, entity, ImBlock, ImMap, Image) {
        var vm = this;

        vm.imBlock = entity;
        vm.previousState = previousState.name;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('imicloudApp:imBlockUpdate', function(event, result) {
            vm.imBlock = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
