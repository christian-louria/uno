function index(){
	$("#mainContent").load("home.html");
}

function hostGame(){
	$("#mainContent").load("hostGame.html");
}

function joinGame(){
	$("#mainContent").load("joinGame.html");
}


$(document).ready(function(){
	window.history.pushState('forward', null, './index');
	
	if (window.history && window.history.pushState) {

    window.history.pushState('forward', null, './#forward');

    $(window).on('popstate', function() {
      alert('Back button was pressed.');
    });

  }



///Home Screen Button
	$(document).on("click", "#hostGameButton", function(){
		window.history.pushState('forward', null, './hostGame');
		hostGame();
	})

	$(document).on("click", "#joinGameButton", function(){
		window.history.pushState('forward', null, './joinGame');
		joinGame();
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