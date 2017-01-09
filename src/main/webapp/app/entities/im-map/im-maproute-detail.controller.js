(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .controller('ImMapRouteDetailController', ImMapRouteDetailController);

    ImMapRouteDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'ImMap', 'ImDocument', 'ImBlock'];

    function ImMapRouteDetailController($scope, $rootScope, $stateParams, previousState, entity, ImMap, ImDocument, ImBlock) {
        var vm = this;

        vm.imMap = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('imicloudApp:imMapUpdate', function(event, result) {
            vm.imMap = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
