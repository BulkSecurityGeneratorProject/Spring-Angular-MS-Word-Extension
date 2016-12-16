(function() {
    'use strict';
    angular
        .module('imicloudApp')
        .factory('ImBlock', ImBlock);

    ImBlock.$inject = ['$resource'];

    function ImBlock ($resource) {
        var resourceUrl =  'api/im-blocks/:id';

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
