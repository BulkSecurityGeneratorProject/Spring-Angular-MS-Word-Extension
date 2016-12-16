(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .controller('ImageRouteDetailController', ImageRouteDetailController);

    ImageRouteDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Image', 'ImBlock'];

    function ImageRouteDetailController($scope, $rootScope, $stateParams, previousState, entity, Image, ImBlock) {
        var vm = this;

        vm.image = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('imicloudApp:imageUpdate', function(event, result) {
            vm.image = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
