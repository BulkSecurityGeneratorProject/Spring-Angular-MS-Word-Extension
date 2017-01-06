(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .controller('BrandingDetailController', BrandingDetailController);

    BrandingDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Branding'];

    function BrandingDetailController($scope, $rootScope, $stateParams, previousState, entity, Branding) {
        var vm = this;

        vm.branding = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('imicloudApp:brandingUpdate', function(event, result) {
            vm.branding = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
