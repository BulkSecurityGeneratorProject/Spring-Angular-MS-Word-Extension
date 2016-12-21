(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .controller('FolderRouteDialogController', FolderRouteDialogController);

    FolderRouteDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Folder', 'Organization', 'ImDocument'];

    function FolderRouteDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Folder, Organization, ImDocument) {
        var vm = this;

        vm.folder = entity;
        vm.clear = clear;
        vm.save = save;
        vm.organizations = Organization.query();
        vm.imdocuments = ImDocument.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.folder.id !== null) {
                Folder.update(vm.folder, onSaveSuccess, onSaveError);
            } else {
                Folder.save(vm.folder, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('imicloudApp:folderUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
