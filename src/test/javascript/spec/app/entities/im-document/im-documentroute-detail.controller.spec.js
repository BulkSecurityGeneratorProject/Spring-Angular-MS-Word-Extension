'use strict';

describe('Controller Tests', function() {

    describe('ImDocument Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockImDocument, MockFolder, MockImMap, MockUser;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockImDocument = jasmine.createSpy('MockImDocument');
            MockFolder = jasmine.createSpy('MockFolder');
            MockImMap = jasmine.createSpy('MockImMap');
            MockUser = jasmine.createSpy('MockUser');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'ImDocument': MockImDocument,
                'Folder': MockFolder,
                'ImMap': MockImMap,
                'User': MockUser
            };
            createController = function() {
                $injector.get('$controller')("ImDocumentRouteDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'imicloudApp:imDocumentUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
