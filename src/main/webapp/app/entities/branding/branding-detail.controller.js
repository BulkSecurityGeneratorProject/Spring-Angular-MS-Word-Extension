(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .controller('BrandingDetailController', BrandingDetailController);

    BrandingDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Branding', 'Image', 'Organization'];

    function BrandingDetailController($scope, $rootScope, $stateParams, previousState, entity, Branding, Image, Organization) {
        var vm = this;

        vm.branding = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('imicloudApp:brandingUpdate', function(event, result) {
            vm.branding = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
