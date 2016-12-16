(function() {
    'use strict';
    angular
        .module('imicloudApp')
        .factory('ImMap', ImMap);

    ImMap.$inject = ['$resource'];

    function ImMap ($resource) {
        var resourceUrl =  'api/im-maps/:id';

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
