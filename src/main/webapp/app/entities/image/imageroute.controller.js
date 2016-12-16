(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .controller('ImageRouteController', ImageRouteController);

    ImageRouteController.$inject = ['$scope', '$state', 'Image', 'ImageSearch'];

    function ImageRouteController ($scope, $state, Image, ImageSearch) {
        var vm = this;

        vm.images = [];
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Image.query(function(result) {
                vm.images = result;
                vm.searchQuery = null;
            });
        }

        function search() {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            ImageSearch.query({query: vm.searchQuery}, function(result) {
                vm.images = result;
                vm.currentSearch = vm.searchQuery;
            });
        }

        function clear() {
            vm.searchQuery = null;
            loadAll();
        }    }
})();
