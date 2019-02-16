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

})