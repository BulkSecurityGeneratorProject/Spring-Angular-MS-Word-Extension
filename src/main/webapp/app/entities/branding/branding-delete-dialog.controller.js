(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .controller('BrandingDeleteController',BrandingDeleteController);

    BrandingDeleteController.$inject = ['$uibModalInstance', 'entity', 'Branding'];

    function BrandingDeleteController($uibModalInstance, entity, Branding) {
        var vm = this;

        vm.branding = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Branding.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
