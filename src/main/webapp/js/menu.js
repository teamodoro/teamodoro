'use strict';

// Declare app level module which depends on views, and components
var menuApp = angular.module('menuApp', []);


menuApp.controller('menuController', function ($scope, $http) {

	$scope.hideMenu = false; // Скрытие/раскрытие меню
	$scope.selection = 'stat'; // Первая открытая вкладка

	// Скрытие меню
	$scope.closedMenu = function(number) {
		$scope.hideMenu = false;
	}

	// Открывается новая вкладка
	$scope.editSelection = function (data) {
      $scope.selection = data;
    }

    // Выбирается активный пункт меню
    $scope.activeSelection = function (data) {
      if ( $scope.selection == data ) return true;
    }

});


