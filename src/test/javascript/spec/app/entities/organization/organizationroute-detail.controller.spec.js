'use strict';

describe('Controller Tests', function() {

    describe('Organization Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockOrganization, MockFolder, MockUser, MockBranding;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockOrganization = jasmine.createSpy('MockOrganization');
            MockFolder = jasmine.createSpy('MockFolder');
            MockUser = jasmine.createSpy('MockUser');
            MockBranding = jasmine.createSpy('MockBranding');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Organization': MockOrganization,
                'Folder': MockFolder,
                'User': MockUser,
                'Branding': MockBranding
            };
            createController = function() {
                $injector.get('$controller')("OrganizationRouteDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'imicloudApp:organizationUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
