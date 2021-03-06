(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .factory('OrganizationSearch', OrganizationSearch);

    OrganizationSearch.$inject = ['$resource'];

    function OrganizationSearch($resource) {
        var resourceUrl =  'api/_search/organizations/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
