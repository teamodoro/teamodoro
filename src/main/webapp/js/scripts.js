$(document).ready(function()
{
	var idPerson = $('#person');
	var idModal = $('#modal');
	var modalParent = $('.modal_activity');

	idPerson.on('click', function(){
		modalParent.toggleClass('active');
	});
	idModal.on('click', function(){
		modalParent.toggleClass('active');
	});

	var right = $('.right'); // Стрелка вправо
	var left = $('.left'); // Стрелка влево
	var lengthBg = $('.bg > div').length; // Кол-во картинок
	var bg = $('.bg'); // Блок с фоновыми картинками
	var bgDiv = bg.find('div'); // Все фоновые картинки
	var activeDiv = 0; // начальный бэкграунд 0

	right.on('click', function() {
		bgDiv.eq(activeDiv).removeClass('active');
		if ( activeDiv + 1 === lengthBg ) {
			activeDiv = 0;
		} else {
			activeDiv += 1;
		}
		bgDiv.eq(activeDiv).addClass('active');
	});

	left.on('click', function() {
		bgDiv.eq(activeDiv).removeClass('active');
		if ( activeDiv - 1 < 0 ) {
			activeDiv = lengthBg - 1;
		} else {
			activeDiv -= 1;
		}
		bgDiv.eq(activeDiv).addClass('active');
	});


	/* TIMER */
	var timeDiv = $('.time');
	var time = 1500;
	var date = new Date(1000*time);
	var minutes;
	var seconds;
	var periodDiv = $('.period');

	// Получаем данные
	var apiJSON;
	function startGet(){
		console.log("Get api json");
		$.getJSON('/api/current', function(data) {
				apiJSON = data;
				refreshConst();
			}
		);
	};
	startGet();

	function refreshConst() {
		var periodName = apiJSON.state.name; // имя периода
		periodDiv.html(periodName); // пишем название периода
		periodDiv.removeClass('green white yellow'); // убираем все возможные цвета
		periodDiv.addClass(apiJSON.options[periodName].color) // добавляем цвет
		time = apiJSON.options[periodName].duration - apiJSON.currentTime; // считаем время
		nullTime();
	}

	// Добавление нулей для времени
	function nullTime() {
		date = new Date(1000*time);
		// Получаем минуты
		minutes = date.getMinutes();
		minutes = ((minutes < 10) ? "0" : "") + minutes; //Если меньше 10 минут, то добавляем нулик
		// Получаем секундэ
		seconds = date.getSeconds();
		seconds = ((seconds < 10) ? "0" : "") + seconds; //Если меньше 10 сепкунд, то добавляем нулик
		// Выводим
		timeDiv.html(minutes + ":" + seconds);
	}

	// таймер на запрос
	setInterval(function(){
		startGet();
	}, 5000);

	// таймер с временем
	setInterval(function() {

		// Вычитаем 1 секундэ
		time -= 1;
		nullTime();

	}, 1000);



});