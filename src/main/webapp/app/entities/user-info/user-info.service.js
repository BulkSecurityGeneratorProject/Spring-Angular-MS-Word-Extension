(function() {
    'use strict';
    angular
        .module('imicloudApp')
        .factory('UserInfo', UserInfo);

    UserInfo.$inject = ['$resource'];

    function UserInfo ($resource) {
        var resourceUrl =  'api/user-infos/:id';

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
