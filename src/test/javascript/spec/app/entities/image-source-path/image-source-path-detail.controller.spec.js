'use strict';

describe('Controller Tests', function() {

    describe('ImageSourcePath Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockImageSourcePath, MockImage, MockImDocument;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockImageSourcePath = jasmine.createSpy('MockImageSourcePath');
            MockImage = jasmine.createSpy('MockImage');
            MockImDocument = jasmine.createSpy('MockImDocument');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'ImageSourcePath': MockImageSourcePath,
                'Image': MockImage,
                'ImDocument': MockImDocument
            };
            createController = function() {
                $injector.get('$controller')("ImageSourcePathDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'imicloudApp:imageSourcePathUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
