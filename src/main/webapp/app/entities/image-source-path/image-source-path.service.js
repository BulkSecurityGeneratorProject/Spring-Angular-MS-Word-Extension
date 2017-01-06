(function() {
    'use strict';
    angular
        .module('imicloudApp')
        .factory('ImageSourcePath', ImageSourcePath);

    ImageSourcePath.$inject = ['$resource'];

    function ImageSourcePath ($resource) {
        var resourceUrl =  'api/image-source-paths/:id';

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
