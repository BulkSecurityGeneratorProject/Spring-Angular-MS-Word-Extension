(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .factory('UserInfoSearch', UserInfoSearch);

    UserInfoSearch.$inject = ['$resource'];

    function UserInfoSearch($resource) {
        var resourceUrl =  'api/_search/user-infos/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
