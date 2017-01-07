'use strict';

describe('Controller Tests', function() {

    describe('UserInfo Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockUserInfo, MockUser, MockOrganization;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockUserInfo = jasmine.createSpy('MockUserInfo');
            MockUser = jasmine.createSpy('MockUser');
            MockOrganization = jasmine.createSpy('MockOrganization');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'UserInfo': MockUserInfo,
                'User': MockUser,
                'Organization': MockOrganization
            };
            createController = function() {
                $injector.get('$controller')("UserInfoDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'imicloudApp:userInfoUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
