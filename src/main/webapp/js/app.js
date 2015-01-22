'use strict';

// Declare app level module which depends on views, and components
var app = angular.module('myApp', []);


app.controller('appController', function ($scope, $http) {

	$scope.countPersonPomidoro = 5; // Кол-во людей сидящих на помидорах
	$scope.periodName = ""; // Имя периода
	$scope.time = {
		seconds : "",
		minutes : ""
	}; // Минуты и секунды
	$scope.fullTime; // Полное время
	$scope.timerIntervalBOOL = false; // Проверка нужно ли работать таймеру

	// Получаение данных с апишки
	$scope.startGet = function() {
		$http.get('/api/current').
			success(function(data, status, headers, config) {
				console.log("Success");
				$scope.refreshConts(data);
				// Если таймер выключен то врубаем его
				if ( !$scope.timerIntervalBOOL ) $scope.timerIntervalBOOL = true;
			}).
			error(function(data, status, headers, config) {
				// called asynchronously if an error occurs
				// or server returns response with an error status.
			});
	}

	// запуск
	$scope.startGet();

	// таймер на запрос
	setInterval(function(){
		$scope.startGet();
	}, 5000);

	// Обновление имени периода, цвета и времени общего
	$scope.refreshConts = function(data) {
		$scope.periodName = data.state.name;
		$scope.periodClass = data.options[data.state.name].color;
		$scope.fullTime = data.options[data.state.name].duration - data.currentTime;
		$scope.JSON = data;
	}

	// Работа над временем
	// Получаем минуты и секунды
	// Если не хватает, то добавляем нолик вначале 
	// Проверка на конец таймера, останавливаем и запускаем мелодию
	$scope.nullTime = function() {
		var time = new Date(1000 * $scope.fullTime);
		$scope.time.minutes = time.getMinutes();
		$scope.time.minutes = (($scope.time.minutes < 10) ? "0" : "") + $scope.time.minutes; //Если меньше 10 минут, то добавляем нулик
		// Получаем секундэ
		$scope.time.seconds = time.getSeconds();
		$scope.time.seconds = (($scope.time.seconds < 10) ? "0" : "") + $scope.time.seconds; //Если меньше 10 сепкунд, то добавляем нулик

		$scope.$digest(); // Проверка на изменение scope

		if ( $scope.time.minutes == '00' && $scope.time.seconds == '00' ) {
			document.getElementById("endTimer").play();
			$scope.timerIntervalBOOL = false;
			console.log("End period");
			$scope.startGet();
		}
	}

	// Таймер с переменной для остановки
	setInterval(function() {
		if ( $scope.timerIntervalBOOL ) {
			$scope.fullTime -= 1;
			$scope.nullTime();
		}
	}, 1000);


	// Фоновые картинки
	$scope.activeDiv = 0; // Начальная картинка
	$scope.countBg = [0,1,2,3,4,5]; // Сколько их всего
	$scope.countLength = $scope.countBg.length; // тоже самое

	// Тыкаем по право стрелке
	$scope.rightClick = function() {
		if ( $scope.activeDiv + 1 === $scope.countLength ) {
			$scope.activeDiv = 0;
		} else {
			$scope.activeDiv += 1;
		}
	}

	// Тыкаем по левой стрелке
	$scope.leftClick = function() {
		if ( $scope.activeDiv - 1 < 0 ) {
			$scope.activeDiv = $scope.countLength - 1;
		} else {
			$scope.activeDiv -= 1;
		}
	}

	// Выбирается нандомная картинка
	$scope.randomBg = function() {
		$scope.activeDiv = Math.floor(Math.random() * (($scope.countLength - 1) - 0 + 1)) + 0;
	}

	// Интервал для рандомной картинки
	setInterval(function(){
		if ( $scope.randomChecked ) $scope.randomBg();
	}, 1000*60);

});


