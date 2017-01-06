(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .controller('BrandingDialogController', BrandingDialogController);

    BrandingDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Branding'];

    function BrandingDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Branding) {
        var vm = this;

        vm.branding = entity;
        vm.clear = clear;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.branding.id !== null) {
                Branding.update(vm.branding, onSaveSuccess, onSaveError);
            } else {
                Branding.save(vm.branding, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('imicloudApp:brandingUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
