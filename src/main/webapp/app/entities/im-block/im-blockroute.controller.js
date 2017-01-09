(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .controller('ImBlockRouteController', ImBlockRouteController);

    ImBlockRouteController.$inject = ['$scope', '$state', 'DataUtils', 'ImBlock', 'ImBlockSearch'];

    function ImBlockRouteController ($scope, $state, DataUtils, ImBlock, ImBlockSearch) {
        var vm = this;

        vm.imBlocks = [];
        vm.openFile = DataUtils.openFile;
        vm.byteSize = DataUtils.byteSize;
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            ImBlock.query(function(result) {
                vm.imBlocks = result;
                vm.searchQuery = null;
            });
        }

        function search() {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            ImBlockSearch.query({query: vm.searchQuery}, function(result) {
                vm.imBlocks = result;
                vm.currentSearch = vm.searchQuery;
            });
        }

        function clear() {
            vm.searchQuery = null;
            loadAll();
        }    }
})();
