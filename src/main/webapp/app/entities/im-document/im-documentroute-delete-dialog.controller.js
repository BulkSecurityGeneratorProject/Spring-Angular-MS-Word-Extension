(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .controller('ImDocumentRouteDeleteController',ImDocumentRouteDeleteController);

    ImDocumentRouteDeleteController.$inject = ['$uibModalInstance', 'entity', 'ImDocument'];

    function ImDocumentRouteDeleteController($uibModalInstance, entity, ImDocument) {
        var vm = this;

        vm.imDocument = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            ImDocument.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
