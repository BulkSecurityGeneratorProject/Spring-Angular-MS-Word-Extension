(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .controller('ImageRouteDialogController', ImageRouteDialogController);

    ImageRouteDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Image', 'ImBlock'];

    function ImageRouteDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Image, ImBlock) {
        var vm = this;

        vm.image = entity;
        vm.clear = clear;
        vm.save = save;
        vm.imblocks = ImBlock.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.image.id !== null) {
                Image.update(vm.image, onSaveSuccess, onSaveError);
            } else {
                Image.save(vm.image, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('imicloudApp:imageUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
