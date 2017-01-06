(function() {
    'use strict';
    angular
        .module('imicloudApp')
        .factory('Branding', Branding);

    Branding.$inject = ['$resource'];

    function Branding ($resource) {
        var resourceUrl =  'api/brandings/:id';

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
