var canvas,
    w,
    h,
    pomidorImg,
    massObj = [],
    maxCountPomidoro = 10,
    matrixText = ["A","B","#","@","&","*","%","[","]","±","<","!","/","?"],
    pomidoroCanvas = false,
    statusColor = "255, 255, 255, ",
    ua;

function init() {
    canvas = document.getElementById("pomidoroCanvas");
    w = window.innerWidth;
    h = window.innerHeight;
    canvas.width  = w; // Ширина игрового поля
    canvas.height = h; // Высота игрового поля

    // canvas.style.marginTop = "-"+h/2+"px";
    // canvas.style.marginLeft = "-"+w/2+"px";

    maxCountPomidoro = w/6;

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

function pomidorObj(x, y, speed, opacity, text) {
    this. live = true;
    this.x = x;
    this.y = y;
    this.speed = speed;

    this.opacity = opacity || 1;
    this.text = text || null;
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
    if ( massObj.length < maxCountPomidoro*6 ) {
        var x = randomFunction(-10, w);
        var y = randomFunction(-10, -25);
        var speed = randomFunction(3, 5);
        massObj.push(new pomidorObj(x, y, speed));

        massObj.push(new pomidorObj(x, y-30, speed, 1, matrixText[randomFunction(0, matrixText.length)] ));
        massObj.push(new pomidorObj(x, y-60, speed, .8, matrixText[randomFunction(0, matrixText.length)] ));
        massObj.push(new pomidorObj(x, y-90, speed, .6, matrixText[randomFunction(0, matrixText.length)] ));
        massObj.push(new pomidorObj(x, y-120, speed, .4, matrixText[randomFunction(0, matrixText.length)] ));
        massObj.push(new pomidorObj(x, y-150, speed, .2, matrixText[randomFunction(0, matrixText.length)] ));
    }
}

function updatePomidoro() {

    for (var i = 0; i < massObj.length - 1; i++) {
        if ( !massObj[i].live ) massObj.splice(i, 1);
    };

    for (var i = 0; i < massObj.length - 1; i++) {
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

window.onload = function(){
    init();
}