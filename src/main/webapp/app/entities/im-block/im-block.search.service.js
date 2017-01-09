(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .factory('ImBlockSearch', ImBlockSearch);

    ImBlockSearch.$inject = ['$resource'];

    function ImBlockSearch($resource) {
        var resourceUrl =  'api/_search/im-blocks/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
