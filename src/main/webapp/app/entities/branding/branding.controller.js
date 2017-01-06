(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .controller('BrandingController', BrandingController);

    BrandingController.$inject = ['$scope', '$state', 'Branding', 'BrandingSearch'];

    function BrandingController ($scope, $state, Branding, BrandingSearch) {
        var vm = this;

        vm.brandings = [];
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Branding.query(function(result) {
                vm.brandings = result;
                vm.searchQuery = null;
            });
        }

        function search() {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            BrandingSearch.query({query: vm.searchQuery}, function(result) {
                vm.brandings = result;
                vm.currentSearch = vm.searchQuery;
            });
        }

        function clear() {
            vm.searchQuery = null;
            loadAll();
        }    }
})();
