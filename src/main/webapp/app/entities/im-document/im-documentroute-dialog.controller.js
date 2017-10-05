(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .controller('ImDocumentRouteDialogController', ImDocumentRouteDialogController);

    ImDocumentRouteDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'ImDocument', 'Folder', 'ImMap', 'User', 'Branding'];

    function ImDocumentRouteDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, ImDocument, Folder, ImMap, User, Branding) {
        var vm = this;

        vm.imDocument = entity;
        vm.clear = clear;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;
        //vm.folders = Folder.query({size: 99999});
        //vm.immaps = ImMap.query({size: 99999});
        //vm.users = User.query({size: 99999});
        vm.brandings = Branding.query({size: 99999});

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
