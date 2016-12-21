(function() {
    'use strict';
    angular
        .module('imicloudApp')
        .factory('Folder', Folder);

    Folder.$inject = ['$resource'];

    function Folder ($resource) {
        var resourceUrl =  'api/folders/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
