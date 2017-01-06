(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .factory('BrandingSearch', BrandingSearch);

    BrandingSearch.$inject = ['$resource'];

    function BrandingSearch($resource) {
        var resourceUrl =  'api/_search/brandings/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
