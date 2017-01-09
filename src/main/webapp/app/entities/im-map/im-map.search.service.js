(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .factory('ImMapSearch', ImMapSearch);

    ImMapSearch.$inject = ['$resource'];

    function ImMapSearch($resource) {
        var resourceUrl =  'api/_search/im-maps/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
