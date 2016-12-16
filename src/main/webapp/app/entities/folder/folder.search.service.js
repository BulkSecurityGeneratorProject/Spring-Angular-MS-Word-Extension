(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .factory('FolderSearch', FolderSearch);

    FolderSearch.$inject = ['$resource'];

    function FolderSearch($resource) {
        var resourceUrl =  'api/_search/folders/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
