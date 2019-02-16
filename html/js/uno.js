//vars

let pixelRatio = window.devicePixelRatio;
let devMode = false;

const updateTime = 15;


let canvas;
let ctx;
let timer;

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
];

class Card{
	constructor(cardData){

	}
}

class Player{
	constructor(name,cards){
		this.name = name;
		this.cards = [];
		for(let i = 0; i < cards.length; i++){
			this.cards = new Card(cards[i]);
		}
	}
}

const mockGame = [
	{
		name: "Alex",
		cards: [
			{
				type: "1"
			}

		]
	}
]

$(document).ready(function(){
	window.history.pushState({}, document.title, "/" + "");




///Home Screen Button
	$(document).on("click", "#hostGameButton", function(){
		$("#mainContent").load("hostGame.html")
	})

	$(document).on("click", "#joinGameButton", function(){
		$("#mainContent").load("joinGame.html")
	})





///Create or join game
	$(document).on("click", "#createRequest", function(){
		var gameName = $("#gameNameText").val()

		/////socket

	})

	$(document).on("click", "#joinRequest", function(){
		var gameName = $("#gameNameText").val()

		//socket
	})
//////////

	canvas = document.getElementById("gameBoard");

	ctx = canvas.getContext("2d");

	let images = document.getElementById("gamePieces");
	images.style.visibility = 'hidden';


	//scale the canvas properly
	scaleCanvas(canvas, ctx, aspectRatio()[0], aspectRatio()[1]);

	//start the canvas updates
	timer = setInterval(drawBoard, updateTime);



})

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

//get the proper scale for all retina devices
function backingScale(context) {
	if ('devicePixelRatio' in window) {
		if (window.devicePixelRatio > 1) {
			return window.devicePixelRatio;
		}
	}
	return 1;
}

function aspectRatio() {
	let $body = $("body");
	let width = $body.width();
	let height = $body.height();
	let ratio = width / height;

	let destinationRatio = 96 / 100;

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

}