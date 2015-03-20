var canvas,
    w,
    h,
    pomidorImg,
    massObj = [],
    maxCountPomidoro = 10,
    matrixText = ["一", "丨", "丶", "丿", "乙", "亅", "二", "亠", "人", "儿", "入", "八", "冂", "冖", "冫", "几", "凵", "刀", "力", "勹", "匕", "匚", "匸", "十", "卜", "卩", "厂", "厶", "又", "口", "囗", "土", "士", "夂", "夊", "夕", "大", "女", "子", "宀", "寸", "小", "尢", "尸", "屮", "山", "巛", "工", "己", "巾", "干", "幺", "广", "廴", "廾", "弋", "弓", "彐", "彡", "彳", "心", "戈", "户", "手", "支", "攴", "文", "斗", "斤", "方", "无", "日", "曰", "月", "木", "欠", "止", "歹", "殳", "毋", "比", "毛", "氏", "气", "水", "火", "爪", "父", "爻", "爿", "片", "牙", "牛", "犬", "玄", "玉", "瓜", "瓦", "甘", "生"],
    pomidoroCanvas = false,
    statusColor = "255, 255, 255, ",
    branchMass = [],
    trueMatrix = false,
    speedStart = 3,
    speedEnd = 5,
    ua;

var liricText = "Эстрагон (вновь останавливаясь). Гиблое дело.Владимир (подходит к нему мелкими шажками, широко расставляя негнущиеся ноги). Мне тоже начинает так казаться. (Молчит, думает.) Сколько лет я гнал от себя эту мысль, все уговаривал себя: Владимир, подумай, может, еще не все потеряно. И опять бросался в бой. (Задумывается, вспоминая о тяготах борьбы. Эстрагону.) Я смотрю, ты опять здесь.Эстрагон. Думаешь?Владимир. Рад тебя снова видеть. Я думал, ты больше не вернешься.Эстрагон. Я тоже.Владимир. Надо как-то отметить нашу встречу. (Задумывается.) А ну-ка, встань, я тебя обниму. (Протягивает Эстрагону руку.)Эстрагон (раздраженно). Погоди, погоди. Пауза.Владимир (оскорбленный, холодно). Позвольте узнать, где Мсье изволил провести ночь?Эстрагон. В канаве.Владимир (в изумлении). В канаве?! Где?Эстрагон (не шевелясь). Там.Владимир. И тебя не били?Эстрагон. Били... Не очень сильно.Владимир. Все те же?Эстрагон. Те же? Не знаю. Пауза.Владимир. Вот я думаю... давно думаю... все спрашиваю себя... во что бы ты превратился... если бы не я... (Решительно.) В жалкую кучу костей, можешь не сомневаться,Эстрагон (задетый за живое). Ну и что?Владимир (подавленно). Для одного человека это слишком. (Пауза. Решительно.) А с другой стороны, вроде кажется, сейчас-то чего расстраиваться попусту. Раньше надо было решать, на целую вечность раньше, еще в 1900 году.Эстрагон. Ладно, хватит. Помоги мне лучше снять эту дрянь.Владимир. Мы с тобой взялись бы за руки и чуть не первыми бросились бы с Эйфелевой башни. Тогда мы выглядели вполне прилично. А сейчас уже поздно - нас и подняться-то на нее не пустят.";
var liricTextLength = liricText.length;
var liricTextTimer = 20;

function init() {
    canvas = document.getElementById("pomidoroCanvas");
    w = window.innerWidth;
    h = window.innerHeight;
    canvas.width  = w; // Ширина игрового поля
    canvas.height = h; // Высота игрового поля

    maxCountPomidoro = w/40;

    for (var i = 0; i < maxCountPomidoro; i++) {
        branchMass.push(false);
    };


    ctx = canvas.getContext("2d"); // Берём контекст
    imageLoad();

}

function imageLoad(){
    pomidorImg = new Image();
    pomidorImg.src = "img/pomidor_oporot_mini.png";
}

function randomFunction( min, max ) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

function pomidorObj(x, y, speed, opacity, text, numberLine) {
    this.live = true;
    this.x = x;
    this.y = y;
    this.speed = speed;

    this.opacity = opacity || 1;
    this.text = text || null;
    this.numberLine = numberLine;
}

var timerCanvas = function() {
  return  window.requestAnimationFrame(update)       || 
          window.webkitRequestAnimationFrame(update) || 
          window.mozRequestAnimationFrame(update)    || 
          window.oRequestAnimationFrame(update)      || 
          window.msRequestAnimationFrame (update)    || 
          function(){
            window.setTimeout(update, 1000 / 60);
          };
};

function clearCanvas(){
    ctx.clearRect(0, 0, w, h);
}

function addPomidoro() {
    for (var i = 0; i < branchMass.length; i++) {
        if ( !branchMass[i] ) {
            branchMass[i] = true;
            var x = i * 43;
            var numberLine = i;
            var y = randomFunction(-10, -25);
            var speed = randomFunction(speedStart, speedEnd);
            massObj.push(new pomidorObj(x, y, speed));
            if ( trueMatrix ) {
                massObj.push(new pomidorObj(x+5, y-10,  speed,  1, matrixText[randomFunction(0, matrixText.length - 1)] ));
                massObj.push(new pomidorObj(x+5, y-60,  speed, .9, matrixText[randomFunction(0, matrixText.length - 1)] ));
                massObj.push(new pomidorObj(x+5, y-110, speed, .8, matrixText[randomFunction(0, matrixText.length - 1)] ));
                massObj.push(new pomidorObj(x+5, y-160, speed, .7, matrixText[randomFunction(0, matrixText.length - 1)] ));
                massObj.push(new pomidorObj(x+5, y-210, speed, .6, matrixText[randomFunction(0, matrixText.length - 1)] ));
                massObj.push(new pomidorObj(x+5, y-260, speed, .5, matrixText[randomFunction(0, matrixText.length - 1)] ));
                massObj.push(new pomidorObj(x+5, y-310, speed, .4, matrixText[randomFunction(0, matrixText.length - 1)] ));
                massObj.push(new pomidorObj(x+5, y-360, speed, .3, matrixText[randomFunction(0, matrixText.length - 1)] ));
                massObj.push(new pomidorObj(x+5, y-410, speed, .2, matrixText[randomFunction(0, matrixText.length - 1)] ));
                massObj.push(new pomidorObj(x+5, y-460, speed, .1, matrixText[randomFunction(0, matrixText.length - 1)], numberLine ));
            } else {
                massObj.push(new pomidorObj(x+5, y-10, speed, 1, liricText.slice(liricTextTimer, liricTextTimer+1) ));
                liricTextTimer++;
                massObj.push(new pomidorObj(x+5, y-60, speed, .9, liricText.slice(liricTextTimer, liricTextTimer+1) ));
                liricTextTimer++;
                massObj.push(new pomidorObj(x+5, y-110, speed, .8, liricText.slice(liricTextTimer, liricTextTimer+1) ));
                liricTextTimer++;
                massObj.push(new pomidorObj(x+5, y-160, speed, .7, liricText.slice(liricTextTimer, liricTextTimer+1) ));
                liricTextTimer++;
                massObj.push(new pomidorObj(x+5, y-210, speed, .6, liricText.slice(liricTextTimer, liricTextTimer+1) ));
                liricTextTimer++;
                massObj.push(new pomidorObj(x+5, y-260, speed, .5, liricText.slice(liricTextTimer, liricTextTimer+1) ));
                liricTextTimer++;
                massObj.push(new pomidorObj(x+5, y-310, speed, .4, liricText.slice(liricTextTimer, liricTextTimer+1) ));
                liricTextTimer++;
                massObj.push(new pomidorObj(x+5, y-360, speed, .3, liricText.slice(liricTextTimer, liricTextTimer+1) ));
                liricTextTimer++;
                massObj.push(new pomidorObj(x+5, y-410, speed, .2, liricText.slice(liricTextTimer, liricTextTimer+1) ));
                liricTextTimer++;
                massObj.push(new pomidorObj(x+5, y-460, speed, .1, liricText.slice(liricTextTimer, liricTextTimer+1), numberLine ));
                liricTextTimer++;
            }


            if ( liricTextTimer >= liricTextLength ) liricTextTimer = 0;
        }
    };
}

function updatePomidoro() {

    for (var i = 0; i < massObj.length; i++) {
        if ( !massObj[i].live ) {
            if ( massObj[i].numberLine >= 0 ) {
                branchMass[massObj[i].numberLine] = false;
            }
            massObj.splice(i, 1);
        }
    };

    for (var i = 0; i < massObj.length; i++) {
        if ( massObj[i].live ) massObj[i].draw();
    };
}

function update() {
    clearCanvas();
    addPomidoro();
    updatePomidoro();
    if ( pomidoroCanvas ) {
        timerCanvas();        
    }
}

pomidorObj.prototype.draw = function() {

    if ( this.text == null || this.text === null || this.text == 'null' ) {
        // Это нужно выводить во всех браузерах кроме сафари, но хз как определить нормально рабуезер
        // ctx.drawImage( pomidorImg, 0, 0, 40, 40, 250, 250, 40, 40 );
        ctx.drawImage( pomidorImg, this.x, this.y, 40, 40 );
        
    } else {
        ctx.fillStyle = "RGBA(" + statusColor + this.opacity + ")";
        ctx.font = "bold 45px Arial";
        ctx.fillText( this.text, this.x, this.y);        
    }

    this.y += this.speed;
    if ( this.y > h ) {
        this.live = false;
    }
}

window.onresize = function() {
    branchMass.length = 0;
    massObj.length = 0;
    w = window.innerWidth;
    h = window.innerHeight;
    maxCountPomidoro = w/40;
    for (var i = 0; i < maxCountPomidoro; i++) {
        branchMass.push(false);
    }

}


window.onload = function(){
    init();
}