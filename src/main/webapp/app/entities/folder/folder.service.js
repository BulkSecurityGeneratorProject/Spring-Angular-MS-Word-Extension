(function() {
    'use strict';
    angular
        .module('imicloudApp')
        .factory('Folder', Folder);

    Folder.$inject = ['$resource', 'DateUtils'];

    function Folder ($resource, DateUtils) {
        var resourceUrl =  'api/folders/:id';

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
