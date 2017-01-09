(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .controller('ImDocumentDialogController', ImDocumentDialogController);

    ImDocumentDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'ImDocument', 'Folder', 'ImMap', 'User'];

    function ImDocumentDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, ImDocument, Folder, ImMap, User) {
        var vm = this;

        vm.imDocument = entity;
        vm.clear = clear;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;
        vm.folders = Folder.query();
        vm.immaps = ImMap.query();
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.imDocument.id !== null) {
                ImDocument.update(vm.imDocument, onSaveSuccess, onSaveError);
            } else {
                ImDocument.save(vm.imDocument, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('imicloudApp:imDocumentUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
