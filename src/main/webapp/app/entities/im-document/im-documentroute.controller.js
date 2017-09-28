(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .controller('ImDocumentRouteController', ImDocumentRouteController);

    ImDocumentRouteController.$inject = ['$scope', '$state', '$cookies', 'DataUtils', 'ImDocument', 'ImDocumentSearch', 'ParseLinks', 'AlertService', 'paginationConstants','Principal'];

    function ImDocumentRouteController ($scope, $state, $cookies, DataUtils, ImDocument, ImDocumentSearch, ParseLinks, AlertService, paginationConstants, Principal) {
        var vm = this;

        vm.imDocuments = [];
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
        vm.showShareDialog = showShareDialog;
        vm.openFile = DataUtils.openFile;
        vm.byteSize = DataUtils.byteSize;
        vm.shareModalDocument = null;
        vm.copyUrlToClipboard = copyUrlToClipboard;

        Principal.identity().then(function(account) {
            var brandingId = account.branding.id;
            vm.editBrandingUrl = '/#/branding/'+brandingId;
        });

        var sortCookieName = 'imdocument-sorting';
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
                ImDocumentSearch.query({
                    query: vm.currentSearch,
                    page: vm.page,
                    size: vm.itemsPerPage,
                    sort: sort()
                }, onSuccess, onError);
            } else {
                ImDocument.query({
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
                    vm.imDocuments.push(data[j]);
                }

                vm.isPageLoading = false;
            }

            function onError(error) {
                AlertService.error(error.data.message);
                vm.isPageLoading = false;
            }
        }

        function showShareDialog(imDocument){
            $('#shareModal').modal('show');

            vm.shareModalDocument = imDocument;

            console.log(imDocument);
        };

        function copyUrlToClipboard(){
            debugger;
            var input = document.querySelector('#copy-input');
            input.setSelectionRange(0, input.value.length + 1);
            var success = null;
            try {
                success = document.execCommand('copy');
            } catch (err) {
                success = false;
            }
        }

        function reset () {
            vm.page = 0;
            vm.imDocuments = [];
            loadAll();
        }

        function loadPage(page) {
            if(!vm.isPageLoading){
                vm.page = page;
                loadAll();
            }
        }

        function clear () {
            vm.imDocuments = [];
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
            vm.imDocuments = [];
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
