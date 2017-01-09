(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .controller('ImDocumentDeleteController',ImDocumentDeleteController);

    ImDocumentDeleteController.$inject = ['$uibModalInstance', 'entity', 'ImDocument'];

    function ImDocumentDeleteController($uibModalInstance, entity, ImDocument) {
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
