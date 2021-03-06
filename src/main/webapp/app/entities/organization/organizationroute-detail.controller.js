(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .controller('OrganizationRouteDetailController', OrganizationRouteDetailController);

    OrganizationRouteDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Organization', 'Folder', 'Branding'];

    function OrganizationRouteDetailController($scope, $rootScope, $stateParams, previousState, entity, Organization, Folder, Branding) {
        var vm = this;

        vm.organization = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('imicloudApp:organizationUpdate', function(event, result) {
            vm.organization = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
