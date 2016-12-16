(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .controller('ImBlockRouteDeleteController',ImBlockRouteDeleteController);

    ImBlockRouteDeleteController.$inject = ['$uibModalInstance', 'entity', 'ImBlock'];

    function ImBlockRouteDeleteController($uibModalInstance, entity, ImBlock) {
        var vm = this;

        vm.imBlock = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            ImBlock.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
