//vars

let pixelRatio = window.devicePixelRatio;
let devMode = true;

const updateTime = 15;


let canvas;
let ctx;
let timer;
let currentPlayer = "Alex";
let turn = "Alex";

const cardURLs = [
"0-blue.svg",
"0-green.svg",
"0-red.svg",
"0-yellow.svg",
"1-blue.svg",
"1-green.svg",
"1-red.svg",
"1-yellow.svg",
"2-blue.svg",
"2-green.svg",
"2-red.svg",
"2-yellow.svg",
"3-blue.svg",
"3-green.svg",
"3-red.svg",
"3-yellow.svg",
"4-blue.svg",
"4-green.svg",
"4-red.svg",
"4-yellow.svg",
"5-blue.svg",
"5-green.svg",
"5-red.svg",
"5-yellow.svg",
"6-blue.svg",
"6-green.svg",
"6-red.svg",
"6-yellow.svg",
"7-blue.svg",
"7-green.svg",
"7-red.svg",
"7-yellow.svg",
"8-blue.svg",
"8-green.svg",
"8-red.svg",
"8-yellow.svg",
"9-blue.svg",
"9-green.svg",
"9-red.svg",
"9-yellow.svg",
"draw-2-blue.svg",
"draw-2-green.svg",
"draw-2-red.svg",
"draw-2-yellow.svg",
"draw-4.svg",
"reverse-blue.svg",
"reverse-green.svg",
"reverse-red.svg",
"reverse-yellow.svg",
"skip-blue.svg",
"skip-green.svg",
"skip-red.svg",
"skip-yellow.svg",
"wild.svg",
"back.svg",
];

const cardRatio = 0.45;
const cardWidth = 240 * cardRatio;
const cardHeight = 360 * cardRatio;

class Card{
	constructor(type, color){
		this.type = type;
		this.color = color;
		this.image = `${this.type}-${this.color}.svg`;
		this.isSelected = false;

		this.x = 0;
		this.y = 0;
		this.width = 0;
		this.height = 0;
	}

}

class Player{
	constructor(name,cards){
		this.name = name;
		this.cards = [];
		for(let i = 0; i < cards.length; i++){
			this.cards.push(new Card(cards[i].type, cards[i].color));
		}
	}

	drawCards(ctx, player){
		let normalizedWidth = canvas.width / devicePixelRatio;
		let normalizedHeight = canvas.height / devicePixelRatio;

		let startOffset = (normalizedWidth - this.cards.length * cardWidth * 0.4) / 2;

		for(let i = 0; i < this.cards.length; i++){
			let card = this.cards[i];
			let img = document.getElementById(card.image);

			let moveUp = card.isSelected ? 80 : 40;

			if(this.name === currentPlayer){
				card.x = startOffset+ i * cardWidth * 0.4;
				card.y = normalizedHeight - cardHeight - moveUp;
			} else if (player === 1) {
				img = document.getElementById("back.svg");
				card.x = startOffset+ i * cardWidth * 0.4;
				card.y = 30;
			} else if (player === 2) {
				ctx.translate(normalizedWidth/2, normalizedHeight/2);
				ctx.rotate(90 * Math.PI / 180);
				ctx.translate(-normalizedWidth/2, -normalizedHeight/2);
				img = document.getElementById("back.svg");
				card.x = startOffset+ i * cardWidth * 0.4;
				card.y = -90;
				
			}
			else if (player === 3) {
				ctx.translate(normalizedWidth/2, normalizedHeight/2);
				ctx.rotate(-90 * Math.PI / 180);
				ctx.translate(-normalizedWidth/2, -normalizedHeight/2);
				img = document.getElementById("back.svg");
				card.x = startOffset+ i * cardWidth * 0.4;
				card.y = -90;
				
			}

			card.width = cardWidth;
			card.height = cardHeight;

			ctx.drawImage(img, card.x, card.y, card.width, card.height);

			if(player === 2){
				ctx.translate(normalizedWidth/2, normalizedHeight/2);
				ctx.rotate(-90 * Math.PI / 180);
				ctx.translate(-normalizedWidth/2, -normalizedHeight/2)
			}
			else if (player === 3) {
				ctx.translate(normalizedWidth/2, normalizedHeight/2);
				ctx.rotate(90 * Math.PI / 180);
				ctx.translate(-normalizedWidth/2, -normalizedHeight/2)
				
				
			}

		}
	}

	registerClick(x,y){
		for(let i = 0; i < this.cards.length; i++) {
			let card = this.cards[i];
			card.isSelected = x > card.x &&
				y > card.y &&
				y < card.y + card.height &&
				x < card.x + card.width * (i === this.cards.length - 1 ? 1 : 0.4);
		}
	}

}

let players = [];

function index(){
	$("#mainContent").load("home.html");
}

function hostGame(){
	$("#mainContent").load("hostGame.html");
}

function joinGame(){
	$("#mainContent").load("joinGame.html");
}

function startGame(){

	$("#mainContent").load("room.html", function(){
		$("#unoTitle").hide();
		canvas = document.getElementById("gameBoard");

		ctx = canvas.getContext("2d");


		//scale the canvas properly
		scaleCanvas(canvas, ctx, aspectRatio()[0], aspectRatio()[1]);

		for(let i = 0; i < mockGame.length; i++){
			players.push(new Player(mockGame[i].name, mockGame[i].cards));
		}

		canvas.addEventListener("click", function(e) {
			e.preventDefault();

			let x;
			let y;
			if (e.pageX || e.pageY) {
				x = e.pageX;
				y = e.pageY;
			}
			else {
				x = e.clientX + document.body.scrollLeft + document.documentElement.scrollLeft;
				y = e.clientY + document.body.scrollTop + document.documentElement.scrollTop;
			}
			x -= canvas.offsetLeft;
			y -= canvas.offsetTop;
			console.log(x)
			console.log(y)

			for(let i = 0; i < players.length; i++){
				players[i].registerClick(x, y);
			}
		}, false);

		//start the canvas updates
		timer = setInterval(drawBoard, updateTime);


	});

}

const mockGame = [
	{
		name: "Alex",
		cards: [
			{
				type: "1",
				color: "red"
			},
			{
				type: "2",
				color: "green"
			},
			{
				type: "3",
				color: "yellow"
			},
			{
				type: "4",
				color: "blue"
			}

		]
	},
	{
		name: "Blex",
		cards: [
			{
				type: "5",
				color: "red"
			},
			{
				type: "5",
				color: "green"
			},
			{
				type: "5",
				color: "yellow"
			},
			{
				type: "5",
				color: "blue"
			}

		]
	},
	{
		name: "Flex",
		cards: [
			{
				type: "9",
				color: "red"
			},
			{
				type: "9",
				color: "green"
			},
			{
				type: "9",
				color: "yellow"
			},
			{
				type: "5",
				color: "blue"
			}

		]
	}
	,
	{
		name: "Sex",
		cards: [
			{
				type: "9",
				color: "red"
			},
			{
				type: "9",
				color: "green"
			},
			{
				type: "9",
				color: "yellow"
			},
			{
				type: "5",
				color: "blue"
			}

		]
	}
];

$(document).ready(function(){

///////////////////////////

//History Controls

///////////////////////////
	window.history.pushState('forward', null, './index');
	if (window.history && window.history.pushState) {
    $(window).on('popstate', function() {
      url = window.location.href
      if (url.includes("index")){
      	index();
      }
      if (url.includes("hostGame")) {
      	hostGame();
      }
      if (url.includes("joinGame")) {
      	joinGame();
      }
    });

  }


///Leave Game Button
	$(document).on("click", "#leaveGameButton", function(){
		if (confirm("Are you sure you want to leave the room")){
			location.reload()
		}
	});



///Home Screen Button
	$(document).on("click", "#hostGameButton", function(){
		window.history.pushState('forward', null, './hostGame');
		hostGame();
	});

	$(document).on("click", "#joinGameButton", function(){
		window.history.pushState('forward', null, './joinGame');
		joinGame();
	});



///Create or join game
	let $gameNameText = $("#gameNameText");
	$(document).on("click", "#createRequest", function(){
		let gameName = $gameNameText.val();

		/////socket
		window.history.pushState('forward', null, './ROOMNAME-TODO');

	});

	$(document).on("click", "#joinRequest", function(){
		let gameName = $gameNameTex.val();

		//socket
		window.history.pushState('forward', null, './ROOMNAME-TODO');
	});
//////////
	//load in images
	for(let i = 0; i < cardURLs.length; i++){
		$("#cards").append(`<img src="/img/cards/SVG/${cardURLs[i]}" id="${cardURLs[i]}"
		 height="0px" width="0px" alt="${cardURLs[i]}">`);
	}

	startGame();

});

//GamePlay

//makes the canvas scale right for retina devices
function scaleCanvas(canvas, context, width, height) {
	// assume the device pixel ratio is 1 if the browser doesn't specify it
	const devicePixelRatio = window.devicePixelRatio || 1;

	// determine the 'backing store ratio' of the canvas context
	const backingStoreRatio = (
		context.webkitBackingStorePixelRatio ||
		context.mozBackingStorePixelRatio ||
		context.msBackingStorePixelRatio ||
		context.oBackingStorePixelRatio ||
		context.backingStorePixelRatio || 1
	);

	// determine the actual ratio we want to draw at
	const ratio = devicePixelRatio / backingStoreRatio;

	if (devicePixelRatio !== backingStoreRatio) {
		// set the 'real' canvas size to the higher width/height
		canvas.width = width * ratio;
		canvas.height = height * ratio;

		// ...then scale it back down with CSS
		canvas.style.width = width + 'px';
		canvas.style.height = height + 'px';
	} else {
		// this is a normal 1:1 device; just scale it simply
		canvas.width = width;
		canvas.height = height;
		canvas.style.width = '';
		canvas.style.height = '';
	}

	// scale the drawing context so everything will work at the higher ratio
	context.scale(ratio, ratio);
}


function aspectRatio() {
	let $body = $("body");
	let width = $body.width();
	let height = $body.height();
	let ratio = width / height;

	let destinationRatio = 1;

	if (ratio === destinationRatio) {
		return [width, height];
	} else if (ratio > destinationRatio) {
		width = width * destinationRatio;
		return [width, height];
	} else if (ratio < destinationRatio) {
		height = height * destinationRatio;
		return [width, height];
	}
}

function drawBoard(){
	let normalizedWidth = canvas.width / devicePixelRatio;
	let normalizedHeight = canvas.height / devicePixelRatio;

	//clear background
	ctx.fillStyle = "#FF0000";
	ctx.beginPath();
	ctx.rect(0,0, normalizedWidth, normalizedHeight);
	ctx.fill();
	ctx.closePath();

	// ctx.translate(normalizedWidth/2, normalizedHeight/2)
	// ctx.rotate(-20 * Math.PI / 180);
	// ctx.translate(-normalizedWidth/2, -normalizedHeight/2)
	// ctx.font = "bolder 100px Arial";
	// ctx.fillStyle = "gold";
	// ctx.textAlign = "center"; 
	// ctx.fillText("UNO", normalizedWidth/1.72, normalizedHeight/5);
	// ctx.lineWidth = "3";
	// ctx.strokeStyle = "white";
	// ctx.strokeText("UNO", normalizedWidth/1.72, normalizedHeight/5);
	// ctx.translate(normalizedWidth/2, normalizedHeight/2)
	// ctx.rotate(20 * Math.PI / 180);
	// ctx.translate(-normalizedWidth/2, -normalizedHeight/2)


	let backCard  = document.getElementById("back.svg");
	//ctx.
	ctx.shadowOffsetX = 5;
	ctx.shadowOffsetY = 5;
	ctx.shadowColor = 'RGBA(38, 42, 44, .5)';
	ctx.shadowBlur = 40;
	ctx.drawImage(backCard, normalizedWidth/2 - 150, normalizedHeight/2  - 50, cardWidth, cardHeight);
	ctx.drawImage(backCard, normalizedWidth/1.98 - 150, normalizedHeight/2.02 - 50, cardWidth, cardHeight);
	ctx.drawImage(backCard, normalizedWidth/1.98 - 150, normalizedHeight/2.02 - 50, cardWidth, cardHeight);
	ctx.drawImage(backCard, normalizedWidth/1.97 - 150, normalizedHeight/2.03 - 50, cardWidth, cardHeight);
	ctx.drawImage(backCard, normalizedWidth/1.99 - 150, normalizedHeight/1.99 - 50, cardWidth, cardHeight);
	ctx.drawImage(backCard, normalizedWidth/1.98 - 150, normalizedHeight/1.98 - 50, cardWidth, cardHeight);
	ctx.drawImage(backCard, normalizedWidth/1.99 - 150, normalizedHeight/1.99 - 50, cardWidth, cardHeight);
	ctx.shadowOffsetX = 0;
	ctx.shadowOffsetY = 0;
	ctx.shadowColor = 'RGBA(0, 0, 0, 0)';
	ctx.shadowBlur = 0;



	for(let i = 0; i < players.length; i++){
		let player = players[i];
		player.drawCards(ctx, i);

	}

}