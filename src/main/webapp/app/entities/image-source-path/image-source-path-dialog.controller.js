(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .controller('ImageSourcePathDialogController', ImageSourcePathDialogController);

    ImageSourcePathDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'ImageSourcePath'];

    function ImageSourcePathDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, ImageSourcePath) {
        var vm = this;

        vm.imageSourcePath = entity;
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
            if (vm.imageSourcePath.id !== null) {
                ImageSourcePath.update(vm.imageSourcePath, onSaveSuccess, onSaveError);
            } else {
                ImageSourcePath.save(vm.imageSourcePath, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('imicloudApp:imageSourcePathUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();