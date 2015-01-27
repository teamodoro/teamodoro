'use strict';

// Declare app level module which depends on views, and components
var app = angular.module('myApp', []);


app.controller('appController', function ($scope, $http) {

	$scope.countPersonPomidoro = 0; // Кол-во людей сидящих на помидорах
	$scope.periodName = ""; // Имя периода
	$scope.periodNameOld = "";
	$scope.time = {
		seconds : "",
		minutes : ""
	}; // Минуты и секунды
	$scope.fullTime; // Полное время
	$scope.timerIntervalBOOL = false; // Проверка нужно ли работать таймеру
	$scope.randomChecked = true;
	$scope.modalPerson = false; // переменная для модального окна
	$scope.pomidoroCanvasAng = false;

	$scope.sessionItems = [];

	$scope.wordPerson = ' пользователей';

	// Получаение данных с апишки
	$scope.startGet = function() {
		$http.get('/api/current').
			success(function(data, status, headers, config) {
				console.log("Success Timer");
				$scope.refreshConts(data);
				// Если таймер выключен то врубаем его
				if ( !$scope.timerIntervalBOOL ) $scope.timerIntervalBOOL = true;


			}).
			error(function(data, status, headers, config) {
				console.log("Error Get Timer");
			});

		$http.get('/api/current/participants').
			success(function(data, status, headers, config) {
				console.log("Success Session");
				$scope.countPersonPomidoro = data.length;
				// data.map(function(person) {
				// 	if (person.name == null) {
				// 		person.name = "Анонимный Садовод"
				// 	}
				// 	return person;
				// })
				$scope.sessionItems = data;
				$scope.wordSplice();
			}).
			error(function(data, status, headers, config) {
				console.log("Error Get Session");
			});
	}

	// запуск
	$scope.startGet();

	// таймер на запрос
	setInterval(function(){
		$scope.startGet();
	}, 5000);


	// Смена окончания слова
	$scope.wordSplice = function() {		
		var count = "" + $scope.countPersonPomidoro + "";

		var aT = count.slice(count.length - 1, count.length);
		var aT2 = count.slice(count.length - 2, count.length);

		if(aT == 0 || aT == 5 || aT == 6 || aT == 7 || aT == 8 || aT == 9 || aT2 == 11)
		{
			$scope.wordPerson = ' пользователей';
		}
		else if(aT == 1 && aT2 != 11)
		{
			$scope.wordPerson = ' пользователь';
		}
		else if(aT == 2 || aT == 3 || aT == 4)
		{
			if(aT2 == 12 || aT2 == 13 || aT2 == 14)
			{
				$scope.wordPerson = ' пользователей';
			}
			else
			{
				$scope.wordPerson = ' пользователя';
			}
		}
	}

	// Обновление имени периода, цвета и времени общего
	$scope.refreshConts = function(data) {
		$scope.periodName = data.state.name; // Именование статуса
		if ( $scope.periodNameOld == "" ) $scope.periodNameOld = $scope.periodName;
		$scope.periodClass = data.options[data.state.name].color; // Цвет статуса
		$scope.fullTime = data.options[data.state.name].duration - data.currentTime; // Текущее время
		$scope.JSON = data;

		if ( $scope.periodClass == 'white' ) statusColor = "242, 242, 242, ";
		if ( $scope.periodClass == 'yellow' ) statusColor = "255, 228, 0, ";
		if ( $scope.periodClass == 'green' ) statusColor = "58, 208, 38, ";
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

		// Изменение тайтла документа
		document.title = $scope.time.minutes + ":" + $scope.time.seconds + " ТОМАТОС ПРОДАКШН";

		$scope.$digest(); // Проверка на изменение scope

		if ( $scope.time.minutes == '00' && $scope.time.seconds == '00' || ( $scope.periodName != $scope.periodNameOld ) ) {
			document.getElementById("endTimer").play();
			$scope.timerIntervalBOOL = false;
			console.log("End period");
			$scope.startGet();
			$scope.tomatos = true;
			$scope.periodNameOld = $scope.periodName

			setTimeout(function(){
				$scope.tomatos = false;
			}, 1200);

			
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

	$scope.startPomidoroCanvas = function() {
		pomidoroCanvas=!pomidoroCanvas;
		$scope.pomidoroCanvasAng=!$scope.pomidoroCanvasAng;
		update();
	}

	$scope.selectBg = function(number) {
		$scope.activeDiv = number;
		$scope.randomChecked = false;
		$scope.hideMenu = false;
	}

});


