(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .controller('FolderRouteDeleteController',FolderRouteDeleteController);

    FolderRouteDeleteController.$inject = ['$uibModalInstance', 'entity', 'Folder'];

    function FolderRouteDeleteController($uibModalInstance, entity, Folder) {
        var vm = this;

        vm.folder = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Folder.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
