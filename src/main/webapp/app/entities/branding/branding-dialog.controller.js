(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .controller('BrandingDialogController', BrandingDialogController);

    BrandingDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'Branding', 'Image','Upload'];

    function BrandingDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, Branding, Image, Upload) {
        var vm = this;

        vm.branding = entity;
        vm.clear = clear;
        vm.save = save;
        vm.logoimages = Image.query({filter: 'branding-is-null'});
        $q.all([vm.branding.$promise, vm.logoimages.$promise]).then(function() {
            if (!vm.branding.logoImageId) {
                return $q.reject();
            }
            return Image.get({id : vm.branding.logoImageId}).$promise;
        }).then(function(logoImage) {
            vm.logoimages.push(logoImage);
        });

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        // upload on file select or drop
        $scope.upload = function (file) {
            Upload.upload({
                url: '/upload/logo/',
                data: {logo_file: file}
            }).then(function (resp) {
                vm.branding.logoImageFilename = resp.data.filename;
                vm.branding.logoUrl = resp.data.url;
                vm.branding.logoImageId = resp.data.imageId;

            }, function (resp) {
                console.log('Error status: ' + resp.status);
            }, function (evt) {
                //var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                //console.log('progress: ' + progressPercentage + '% ' + evt.config.data.file.name);
            });
        };

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.branding.id !== null) {
                Branding.update(vm.branding, onSaveSuccess, onSaveError);
            } else {
                Branding.save(vm.branding, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('imicloudApp:brandingUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
