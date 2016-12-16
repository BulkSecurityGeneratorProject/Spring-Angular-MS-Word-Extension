(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .controller('ImBlockRouteDialogController', ImBlockRouteDialogController);

    ImBlockRouteDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'ImBlock', 'ImMap', 'Image'];

    function ImBlockRouteDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, ImBlock, ImMap, Image) {
        var vm = this;

        vm.imBlock = entity;
        vm.clear = clear;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;
        vm.immaps = ImMap.query();
        vm.images = Image.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.imBlock.id !== null) {
                ImBlock.update(vm.imBlock, onSaveSuccess, onSaveError);
            } else {
                ImBlock.save(vm.imBlock, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('imicloudApp:imBlockUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
