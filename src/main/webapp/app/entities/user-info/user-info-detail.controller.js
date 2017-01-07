(function() {
    'use strict';

    angular
        .module('imicloudApp')
        .controller('UserInfoDetailController', UserInfoDetailController);

    UserInfoDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'UserInfo', 'User', 'Organization'];

    function UserInfoDetailController($scope, $rootScope, $stateParams, previousState, entity, UserInfo, User, Organization) {
        var vm = this;

        vm.userInfo = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('imicloudApp:userInfoUpdate', function(event, result) {
            vm.userInfo = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
