(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .controller('OrganizationRouteDialogController', OrganizationRouteDialogController);

    OrganizationRouteDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'Organization', 'Folder', 'Branding'];

    function OrganizationRouteDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, Organization, Folder, Branding) {
        var vm = this;

        vm.organization = entity;
        vm.clear = clear;
        vm.save = save;
        vm.folders = Folder.query();
        vm.brandings = Branding.query({filter: 'organization-is-null'});
        $q.all([vm.organization.$promise, vm.brandings.$promise]).then(function() {
            if (!vm.organization.brandingId) {
                return $q.reject();
            }
            return Branding.get({id : vm.organization.brandingId}).$promise;
        }).then(function(branding) {
            vm.brandings.push(branding);
        });

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.organization.id !== null) {
                Organization.update(vm.organization, onSaveSuccess, onSaveError);
            } else {
                Organization.save(vm.organization, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('imicloudApp:organizationUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
