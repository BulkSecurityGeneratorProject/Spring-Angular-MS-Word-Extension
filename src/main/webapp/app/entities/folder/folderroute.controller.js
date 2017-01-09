(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .controller('FolderRouteController', FolderRouteController);

    FolderRouteController.$inject = ['$scope', '$state', 'Folder', 'FolderSearch'];

    function FolderRouteController ($scope, $state, Folder, FolderSearch) {
        var vm = this;

        vm.folders = [];
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Folder.query(function(result) {
                vm.folders = result;
                vm.searchQuery = null;
            });
        }

        function search() {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            FolderSearch.query({query: vm.searchQuery}, function(result) {
                vm.folders = result;
                vm.currentSearch = vm.searchQuery;
            });
        }

        function clear() {
            vm.searchQuery = null;
            loadAll();
        }    }
})();
