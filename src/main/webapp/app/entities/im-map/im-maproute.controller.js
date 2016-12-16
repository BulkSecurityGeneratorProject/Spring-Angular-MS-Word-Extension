(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .controller('ImMapRouteController', ImMapRouteController);

    ImMapRouteController.$inject = ['$scope', '$state', 'ImMap', 'ImMapSearch'];

    function ImMapRouteController ($scope, $state, ImMap, ImMapSearch) {
        var vm = this;

        vm.imMaps = [];
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            ImMap.query(function(result) {
                vm.imMaps = result;
                vm.searchQuery = null;
            });
        }

        function search() {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            ImMapSearch.query({query: vm.searchQuery}, function(result) {
                vm.imMaps = result;
                vm.currentSearch = vm.searchQuery;
            });
        }

        function clear() {
            vm.searchQuery = null;
            loadAll();
        }    }
})();
