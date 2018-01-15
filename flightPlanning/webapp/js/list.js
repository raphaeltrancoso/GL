$(function(){
	// Verif si objet passé en param est null ou non
	function isEmpty(object){
		return object == null || object.length < 1;
	}

	// redirection page login si user non connecté
	function redirectStatus(xhr){
		// If we get a response status code of type "UNAUTHORIZED"
		// from server side (result on a redirection to login page).
		if(xhr.status == 401)
			window.location.href = "/";
	}

    var airplaneList;
    var airplaneType;
    var curPage = 1;
    var maxPage;
    var myRole;

    function callGetAirplaneDone(result){
		airplaneList = $.parseJSON(JSON.stringify(result));
		$("body").show();
		getServerData("ws/flights?page=" + curPage, callGetDone);
    }

    function fillTemplate(flightList, bool){
    	if(bool)
    	{
    		var templateLine = _.template($('#template-line-insertion').html());
    		sessionStorage.myValue = JSON.stringify(flightList[0].atc);
			$(".table-content").empty();
			for(var i = 0; i<flightList.length; i++){ 
			    getAirplaneType(flightList[i].idAirplane);
			    var html = templateLine({
					"commercialNumber": flightList[i].commercialNumber,
					"atcNumber": flightList[i].atc,
					"airplane": airplaneType,
					"oaciDeparture": flightList[i].oaciDeparture,
					"oaciDestination": flightList[i].oaciDestination,
					"departure": toValidDate(flightList[i].departure),
					"arrivalTime": toValidDate(flightList[i].arrivalTime)
			    });
			    $(".table-content").append(html);
			}
			var width = $("table").width();
    	}
    	else
    	{
    		var templateLine = _.template($('#template-line-insertion-member').html());
    		sessionStorage.myValue = JSON.stringify(flightList[0].atc);
			$(".table-content").empty();
			for(var i = 0; i<flightList.length; i++){ 
			    getAirplaneType(flightList[i].idAirplane);
			    var html = templateLine({
			    	"commercialNumber": flightList[i].commercialNumber,
					"oaciDeparture": flightList[i].oaciDeparture,
					"oaciDestination": flightList[i].oaciDestination,
					"departure": toValidDateMember(flightList[i].departure),
					"arrivalTime": toValidDateMember(flightList[i].arrivalTime)
			    });
			    $(".table-content").append(html);
			}
			var width = $("#tableMember table").width();
    	}
    	$(".pagination").width(width).css("margin", "0px auto 40px");
	    if(maxPage > 1)
	    	$("#pagi-center span").replaceWith("/ " + maxPage + " pages");
	    else
	    	$("#pagi-center span").replaceWith("/ " + maxPage + " page");
    	disablePagiArrows();
	    $("#page").val(curPage);
    }

    // formatage Date
    function toValidDate(ms){
    	var val = ms;
    	if (typeof ms === 'string')
    		val = parseInt(ms);
    	var d = new Date(val);
    	var index = d.toUTCString().indexOf(',') + 2;
    	return d.toUTCString().substring(index);
    }

    // formatage Date
    function toValidDateMember(ms){
    	var val = ms;
    	if (typeof ms === 'string')
    		val = parseInt(ms);
    	var d = new Date(val);
    	var index = d.toUTCString().indexOf(',') + 2;
    	var index2 = ((d.toUTCString()).length)-4;
    	return d.toUTCString().substring(index,index2);
    }

    function getAirplaneType(id){
    	if(id == "")
    		airplaneType = "-";
    	else
    	{
    		for(var i = 0; i<airplaneList.length; i++)
		    	if(airplaneList[i].id == id)
					airplaneType = airplaneList[i].type;
    	}
    }

    function createSearchURI() {
    	var results = "";
    	var elts = document.getElementsByTagName('input');
    	for (var i = 0; i < elts.length; i++){
    		if (elts[i].value != "")
    			results += elts[i].getAttribute('id') + "=" + elts[i].value;
    		if (i+1 < elts.length && elts[i+1].value != "")
    			results += "&";
    	}
    	return results;
    } 

    function callGetDone(result){

		var flightList = $.parseJSON(JSON.stringify(result)); 

		// Set default value if flightList is empty
		if(isEmpty(flightList))
		{
			$(".table-content").empty();
			$(".table-content").append('<tr><td colspan="7">No data available.</td></tr>');
		}
		else
		{
			if(myRole == 'CCO')
				fillTemplate(flightList, 1);
			else if(myRole == 'Member')
				fillTemplate(flightList, 0);
		}	    
    }

    function callGetFlightsQuantityDone(result){
    	maxPage = Math.ceil(result/10);
    	getAirplaneData("ws/airplanes",callGetAirplaneDone);
    }

    function getFlightsQuantity(url, success){
    	$.ajax({
		    dataType:"json",
			url:url,
		    type:"GET",
		    success: success,
		    error: redirectStatus
		})
    }

    function getAirplaneData(url, success){
		$.ajax({
		    dataType:"json",
			url:url,
		    type:"POST",
		    success: success,
		    error: redirectStatus
		})
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

    function deletePlane(url){
		$.ajax({
		    dataType:"json",
		    url:url,
		    type:"DELETE",
		    success: function(){
		    	$("#table-content tr").remove();
		    	getServerData("ws/flights?page=" + curPage,callGetDone);
		    }
		}).fail(function() {
			alert( "error" );
    	});
    }

    function searchDone(result){
		var flightList = $.parseJSON(JSON.stringify(result));   

		$(".table-content").empty();
		if(isEmpty(flightList))
		{
			$(".table-content").empty();
			$(".table-content").append('<tr><td colspan="7">No data available.</td></tr>');
		}
		else
		{
			if(myRole == 'CCO')
	    	{
	    		fillTemplate(flightList, 1);
	    	}
			else if(myRole == 'Member')
			{
				fillTemplate(flightList, 0);
			}
		}
			
		var width = $("thead").width();
	    $(".pagination").width(width).css("margin", "0px auto 40px");
    }

    /* Disable the pagination left arrow which show to the user that it's not 
    	possible to use it again */
    function disablePagiArrows(){
    	var arrowLeft = $('#pagi-left');
    	var arrowRight = $('#pagi-right');
    	if (parseInt(curPage) <= 1) {
			arrowLeft.css("opacity","0.3");
			arrowLeft.css("pointer-events","none"); 
		} else {
			arrowLeft.css("opacity","1");
			arrowLeft.css("pointer-events","visible");  
		}
		if (parseInt(curPage) >= maxPage) {
			arrowRight.css("opacity","0.3");
			arrowRight.css("pointer-events","none"); 
		} else {
			arrowRight.css("opacity","1");
			arrowRight.css("pointer-events","visible");  
		}
    }

    $("body").hide();
    $('.search-container').hide();

   	$('#tableMember, #tableCCO').hide();
   	myRole = sessionStorage.getItem('myRole');
   	if(myRole=='CCO')
   		$('#tableCCO').show();
   	else if(myRole=='Member')
	{
		$('#tableMember').show();
		$('#pagi-right').width('23%');
		$('#menu ul li:first').hide();
	}

    getFlightsQuantity("ws/flights/number", callGetFlightsQuantityDone);

    $("#refresh").click(function(event){
		getServerData("ws/flights?page=" + curPage, callGetDone);
		$('#comNb, #oaciD, #oaciA, #atcNb, #plane, #timeD, #timeA').val("");
	});


	$("#comNb, #oaciD, #oaciA, #atcNb, #plane, #timeD, #timeA").keyup(function(event){
		if($(this).val().length > 1)
			getServerData("ws/flights/search?" + createSearchURI(),searchDone);
		else if($(this).val().length == 0)
			getServerData("ws/flights/search?" + createSearchURI(),searchDone);

	});


	$('#menu ul li').click(function(evt){ // Init Click funtion on Menu
		evt.preventDefault();
		var clicked_ref = $(this).find('a').attr('href'); // Strore Href value
		var clicked_a = $(this).find('a');
      
		switch(clicked_ref)
		{
			case 'display':
				if(clicked_a.attr('value') ==="display")
				{
					clicked_a.attr('value', 'mask');
					$('.search-container').slideDown();
				}
				else if(clicked_a.attr('value') === "mask")
				{
					clicked_a.attr('value', 'display');
					$('.search-container').slideUp('fast');
				}
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

	// gestion click on edit or delete img from table
	$(".table-content").on("click", ".clickable-row", function(e) {
	    var item2 = $(this).closest("tr");
	    var item = $(this).closest("tr")   // Finds the closest row <tr> 
		.find("td");          // Retrieves the text within <td>

		var numComm = item.eq(0).text();
	    numComm=numComm.trim(); // delete space
		if(myRole=='CCO')
		{

			var str = item.eq(3).text();
		    str=str.split(" - ");
		    var oaci = str[0];
		    oaci=oaci.trim();
		    var departure = item.eq(4).text();
		    departure=new Date(departure.trim()).getTime();
		}	
		else
	    {
	    	var str = item.eq(1).text();
		    str=str.split(" - ");
		    var oaci = str[0];
		    oaci=oaci.trim();
		    var departure = item.eq(2).text();
		    departure=new Date(departure.trim()+' GMT').getTime();
	    }

	    var elem, evt = e ? e:event;
	    if (evt.srcElement)  elem = evt.srcElement;
	    else if (evt.target) elem = evt.target;

	    if(elem.tagName=='IMG')
	    {
	    	if(elem.getAttribute('class')=="edit")
	    	{
	    		var triplet = JSON.stringify({"departure": departure,"oaciDeparture": oaci,"commercialNumber": numComm});
				sessionStorage.setItem('myID', triplet);
				window.location.href = "edit.html";
	    	}
			if(elem.getAttribute('class')=="delete")
			{
				item2.children('td').animate({
					padding:0,
					margin:0
				}).wrapInner('<div />').slideUp(function(){ $(this).closest('tr').remove();});
				
				setTimeout(function(){deletePlane("/ws/flights/"+departure+"_"+oaci+"_"+numComm)},250);
			}
	    }
	    else{
		   var triplet = JSON.stringify({"departure": departure,"oaciDeparture": oaci,"commercialNumber": numComm});
		   sessionStorage.setItem('myID', triplet);
		   window.location.href = "info.html";
		}
 
	});

	/* pagination */

	// when user click, show border, else hid'em
	$('#page').focus(function(e){
		$(this).css("border-style","solid");
	});
	$('#page').focusout(function(e){
		$(this).css("border-style","none");
	});

	$('#page').keyup(function(e){
		var page = $(this).val();
		var filter = /^[0-9]+$/;
		if (filter.test(page)){
			curPage = $(this).val();
			if(curPage != 0) // avoid reloading all flights
				getServerData("ws/flights?page=" + curPage, callGetDone);	
		}
		disablePagiArrows();
	});

	$('#pagi-right').on('click', function(){
		if(curPage < maxPage)
		{
			curPage = parseInt(curPage) + 1;
			$('#page').val(curPage);
			getServerData("ws/flights?page=" + curPage, callGetDone); 
		}
	});

	$('#pagi-left').on('click', function(){
		if (curPage > 1){
			curPage = parseInt(curPage) - 1;
			$('#page').val(curPage);
			getServerData("ws/flights?page=" + curPage, callGetDone);
		}
	});
});
