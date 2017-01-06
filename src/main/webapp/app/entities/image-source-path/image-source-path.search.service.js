(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .factory('ImageSourcePathSearch', ImageSourcePathSearch);

    ImageSourcePathSearch.$inject = ['$resource'];

    function ImageSourcePathSearch($resource) {
        var resourceUrl =  'api/_search/image-source-paths/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
