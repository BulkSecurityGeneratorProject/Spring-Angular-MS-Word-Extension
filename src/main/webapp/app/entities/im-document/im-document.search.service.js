(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .factory('ImDocumentSearch', ImDocumentSearch);

    ImDocumentSearch.$inject = ['$resource'];

    function ImDocumentSearch($resource) {
        var resourceUrl =  'api/_search/im-documents/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
