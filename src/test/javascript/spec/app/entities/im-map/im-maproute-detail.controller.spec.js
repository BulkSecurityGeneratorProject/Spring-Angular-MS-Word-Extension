'use strict';

describe('Controller Tests', function() {

    describe('ImMap Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockImMap, MockImDocument, MockImBlock;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockImMap = jasmine.createSpy('MockImMap');
            MockImDocument = jasmine.createSpy('MockImDocument');
            MockImBlock = jasmine.createSpy('MockImBlock');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'ImMap': MockImMap,
                'ImDocument': MockImDocument,
                'ImBlock': MockImBlock
            };
            createController = function() {
                $injector.get('$controller')("ImMapRouteDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'imicloudApp:imMapUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
