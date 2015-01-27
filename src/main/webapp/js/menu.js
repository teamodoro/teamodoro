'use strict';

// Declare app level module which depends on views, and components
var menuApp = angular.module('menuApp', []);


menuApp.controller('menuController', function ($scope, $http) {

	$scope.hideMenu = false;
	$scope.selection = 'stat';
	$scope.closedMenu = function(number) {
		$scope.hideMenu = false;
	}

	$scope.editSelection = function (data) {
      $scope.selection = data;
    }

    $scope.activeSelection = function (data) {
      if ( $scope.selection == data ) return true;
    }

});


