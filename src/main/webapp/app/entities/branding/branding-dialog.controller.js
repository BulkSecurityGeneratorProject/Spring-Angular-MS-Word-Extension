(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .controller('BrandingDialogController', BrandingDialogController);

    BrandingDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'Branding', 'Image'];

    function BrandingDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, Branding, Image) {
        var vm = this;

        vm.branding = entity;
        vm.clear = clear;
        vm.save = save;
        vm.logoimages = Image.query({filter: 'branding-is-null'});
        $q.all([vm.branding.$promise, vm.logoimages.$promise]).then(function() {
            if (!vm.branding.logoImageId) {
                return $q.reject();
            }
            return Image.get({id : vm.branding.logoImageId}).$promise;
        }).then(function(logoImage) {
            vm.logoimages.push(logoImage);
        });

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
