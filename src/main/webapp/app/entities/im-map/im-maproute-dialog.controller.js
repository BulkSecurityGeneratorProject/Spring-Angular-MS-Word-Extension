(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .controller('ImMapRouteDialogController', ImMapRouteDialogController);

    ImMapRouteDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'ImMap', 'ImDocument', 'ImBlock'];

    function ImMapRouteDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, ImMap, ImDocument, ImBlock) {
        var vm = this;

        vm.imMap = entity;
        vm.clear = clear;
        vm.save = save;
        vm.imdocuments = ImDocument.query();
        vm.imblocks = ImBlock.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.imMap.id !== null) {
                ImMap.update(vm.imMap, onSaveSuccess, onSaveError);
            } else {
                ImMap.save(vm.imMap, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('imicloudApp:imMapUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
