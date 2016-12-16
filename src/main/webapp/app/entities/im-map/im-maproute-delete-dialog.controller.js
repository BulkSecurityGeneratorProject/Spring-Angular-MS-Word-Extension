(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .controller('ImMapRouteDeleteController',ImMapRouteDeleteController);

    ImMapRouteDeleteController.$inject = ['$uibModalInstance', 'entity', 'ImMap'];

    function ImMapRouteDeleteController($uibModalInstance, entity, ImMap) {
        var vm = this;

        vm.imMap = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            ImMap.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
