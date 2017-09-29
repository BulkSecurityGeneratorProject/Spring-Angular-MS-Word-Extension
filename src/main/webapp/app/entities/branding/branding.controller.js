(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .controller('BrandingController', BrandingController);

    BrandingController.$inject = ['$scope', '$state', '$cookies', 'DataUtils', 'Branding', 'BrandingSearch', 'ParseLinks', 'AlertService', 'paginationConstants','Principal'];

    function BrandingController ($scope, $state, $cookies, DataUtils, Branding, BrandingSearch, ParseLinks, AlertService, paginationConstants, Principal) {
        var vm = this;

        vm.brandings = [];
        vm.loadPage = loadPage;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.isPageLoading = false;
        vm.page = 0;
        vm.links = {
            last: 0
        };
        vm.predicate = 'id';
        vm.reset = reset;
        vm.reverse = true;
        vm.clear = clear;
        vm.loadAll = loadAll;
        vm.search = search;
        vm.openFile = DataUtils.openFile;
        vm.byteSize = DataUtils.byteSize;

        Principal.identity().then(function(account) {
            var brandingId = account.branding.id;
            vm.editBrandingUrl = '/#/branding/'+brandingId;
        });

        var sortCookieName = 'branding-sorting';
        var sortingConfig = $cookies.getObject(sortCookieName);
        if(sortingConfig && sortingConfig.predicate){
            vm.predicate = sortingConfig.predicate;
            vm.reverse = true && sortingConfig.reverse;
        }

        // Watch for sorting changes
        $scope.$watch('vm.predicate', function(newValue, oldValue) {
            saveSortingToCookie();
        });
        $scope.$watch('vm.reverse', function(newValue, oldValue) {
            saveSortingToCookie();
        });


        function saveSortingToCookie(){
            var sortingConfig = {
                predicate: vm.predicate,
                reverse: vm.reverse
            };
            $cookies.putObject(sortCookieName, sortingConfig);
        }


        loadAll();

        function loadAll () {
            vm.isPageLoading = true;
            if (vm.currentSearch) {
                BrandingSearch.query({
                    query: vm.currentSearch,
                    page: vm.page,
                    size: vm.itemsPerPage,
                    sort: sort()
                }, onSuccess, onError);
            } else {
                Branding.query({
                    page: vm.page,
                    size: vm.itemsPerPage,
                    sort: sort()
                }, onSuccess, onError);
            }
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'id') {
                    result.push('id');
                }
                return result;
            }

            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');

                for( var j = 0; j < data.length; j++){
                    vm.brandings.push(data[j]);
                }

                vm.isPageLoading = false;
            }

            function onError(error) {
                AlertService.error(error.data.message);
                vm.isPageLoading = false;
            }
        }

        function reset () {
            vm.page = 0;
            vm.brandings = [];
            loadAll();
        }

        function loadPage(page) {
            if(!vm.isPageLoading){
                vm.page = page;
                loadAll();
            }
        }

        function clear () {
            vm.brandings = [];
            vm.links = {
                last: 0
            };
            vm.page = 0;
            vm.predicate = 'id';
            vm.reverse = true;
            vm.searchQuery = null;
            vm.currentSearch = null;
            vm.loadAll();
        }

        function search (searchQuery) {
            if (!searchQuery){
                return vm.clear();
            }
            vm.brandings = [];
            vm.links = {
                last: 0
            };
            vm.page = 0;
            vm.predicate = '_score';
            vm.reverse = false;
            vm.currentSearch = searchQuery;
            vm.loadAll();
        }
    }
})();
