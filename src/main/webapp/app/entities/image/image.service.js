(function() {
    'use strict';
    angular
        .module('imicloudApp')
        .factory('Image', Image);

    Image.$inject = ['$resource', 'DateUtils'];

    function Image ($resource, DateUtils) {
        var resourceUrl =  'api/images/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.createdAt = DateUtils.convertDateTimeFromServer(data.createdAt);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
