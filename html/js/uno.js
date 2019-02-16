$(document).ready(function(){
	window.history.pushState({}, document.title, "/" + "");


	$(document).on("click", "#hostGameButton", function(){
		$("#mainContent").load("hostGame.html")
	})
})