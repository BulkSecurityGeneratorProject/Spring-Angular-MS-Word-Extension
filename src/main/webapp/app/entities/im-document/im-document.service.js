(function() {
    'use strict';
    angular
        .module('imicloudApp')
        .factory('ImDocument', ImDocument);

    ImDocument.$inject = ['$resource', 'DateUtils'];

    function ImDocument ($resource, DateUtils) {
        var resourceUrl =  'api/im-documents/:id';

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
