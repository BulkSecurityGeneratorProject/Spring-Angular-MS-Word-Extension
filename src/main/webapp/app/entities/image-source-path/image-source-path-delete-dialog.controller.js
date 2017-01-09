(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .controller('ImageSourcePathDeleteController',ImageSourcePathDeleteController);

    ImageSourcePathDeleteController.$inject = ['$uibModalInstance', 'entity', 'ImageSourcePath'];

    function ImageSourcePathDeleteController($uibModalInstance, entity, ImageSourcePath) {
        var vm = this;

        vm.imageSourcePath = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            ImageSourcePath.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
