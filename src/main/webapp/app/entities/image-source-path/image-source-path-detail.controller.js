(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .controller('ImageSourcePathDetailController', ImageSourcePathDetailController);

    ImageSourcePathDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'ImageSourcePath'];

    function ImageSourcePathDetailController($scope, $rootScope, $stateParams, previousState, entity, ImageSourcePath) {
        var vm = this;

        vm.imageSourcePath = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('imicloudApp:imageSourcePathUpdate', function(event, result) {
            vm.imageSourcePath = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
