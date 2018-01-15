var ofp,notam,weather;

function redirectStatus(xhr){
	// If we get a response status code of type "UNAUTHORIZED"
	// from server side (result on a redirection to login page).
	if(xhr.status == 401)
	    window.location.href = "/";
}

/* set the attribute data of object tag dynamcically */
function changeData(newURL){
	if(!document.getElementById("htmlClass"))
		return false;
	document.getElementById("htmlClass").setAttribute('data', newURL);
}

/* Permet de désactiver un onglet actif pour 
	activer l'onglet passé en paramètre */
function activeTab(elClass) {
  $(".tabActive").removeClass("tabActive"); 
  $(elClass).addClass("tabActive"); 
}

function desactiveSidebar(){
	$('#sidebar').removeClass('visible');
	$('#contenu').removeClass('visible');
}

$(document).ready(function(){

	/* Permet d'activer l'onglet qui a été la cible du clic */
	$('.li_style').on("click", function(event){
		activeTab(event.target);
	});

	$('#sidebar-btn').click(function(){
		$('#sidebar').toggleClass('visible');
		$('#contenu').toggleClass('visible');	
	});

	$("#details").click(function(event){
		changeData("flightDetails.html");
		desactiveSidebar();
	});
	
	$("#weather").click(function(event){	
		 changeData(weather);
		 desactiveSidebar();		
	});

	$("#ofp").click(function(event){	
		 changeData(ofp);	
		 desactiveSidebar();	
	});

	$("#notam").click(function(event){
		 changeData(notam);	
		 desactiveSidebar();	
	});
});

function replaceSpaceByPlus(str){
	if(typeof str === 'string')
		return str.split(' ').join('+');
}

function getFlightData(url, success){
  $.ajax({
    dataType:"json",
    url:url,
    type:"GET",
    error: redirectStatus
  }).done(success);
}

function getFlightDetails(data){
  $("body").show();
  var chaine = JSON.stringify(data);

  var flight; 
  if (chaine == undefined || chaine == null){
    flight={ commercialNumber : "bonjour" };
  }else {
    flight=$.parseJSON(chaine);
  }	
  weather= "../pdf/" + flight.meteo;
  ofp= "../pdf/" + flight.ofp;
  notam= "../notam/" + flight.notam;
}

function getServerData(url, success){
	$.ajax({
	    dataType:"json",
	    url:url,
	    type:"POST",
	    success: success,
	    error: redirectStatus
	})
}

$(function(){

	myRole = sessionStorage.getItem('myRole');
   	if(myRole=='Member')
	{
		$("#menu ul li:nth-child(2), #sidebar-btn").hide();
	}
	$("body").hide();
	var id = JSON.parse(sessionStorage.getItem('myID'));
    var idpart = $.parseJSON(JSON.stringify(id));

    var oaci = replaceSpaceByPlus(idpart.oaciDeparture);
    var commercial = replaceSpaceByPlus(idpart.commercialNumber);

    var url = "ws/flights/"+idpart.departure+"_"+oaci+"_"+commercial;

   	getFlightData(url,getFlightDetails);

   	$('#menu ul li').click(function(evt){ // Init Click funtion on Menu
      evt.preventDefault();
      var clicked_ref = $(this).find('a').attr('href'); // Strore Href value
      var clicked_a = $(this).find('a');
      //console.log(clicked_ref);
      switch(clicked_ref)
      {
      	case 'list.html':
      		window.location.href = "list.html";
      		break;
      	case 'add.html':
      		window.location.href = "add.html";
      		break;
      	case 'index.html':
      		getServerData("ws/logout", function(){
				window.location.href = "/";
			});
			break;
		default:
			console.log("Error click");
			break;
      }
      
    })
});
