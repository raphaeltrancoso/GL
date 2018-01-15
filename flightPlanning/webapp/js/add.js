function getLongFromDate(date)
{
	var timesplit=date.split(" ");
	var date1=timesplit[0];
	var date2=timesplit[1];
	var year = date1.split("-")[0];
	var month = date1.split("-")[1];
	var day = date1.split("-")[2];
	var hour = date2.split(":")[0];
	var min = date2.split(":")[1];
	
	return new Date(year,month-1,day,hour,min,0,0).getTime();
}

function populateSelect(target, list){
    if (!target || list.length<=0 ){
        return false;
    }
    else {
        
        select = document.getElementById(target);
        
        for (var i = 0; i<list.length; i++){
            var opt = document.createElement('option');
            if(target==='arrApt' || target==='depApt' )
            {
            	opt.value = list[i].oaci;
            	opt.innerHTML = list[i].name;
            }
            else if(target === 'selectPilot' || target === 'selectCopilot')
            {
            	opt.value = list[i].login;
            	opt.innerHTML = list[i].login;
            }
            else if(target === 'selectStaff')
            {
            	opt.value = list[i].login;
            	opt.innerHTML = list[i].firstName;
            }
            else{
            	opt.value = i;
            	opt.innerHTML = list[i].type;
            }
            select.appendChild(opt);
        }
    }
}

function putServerData(url)
{
    var atc = document.getElementById("atc").value;
    var departure = getLongFromDate(document.getElementById("plndDep").value);
    var arrival = getLongFromDate(document.getElementById("plndArr").value);
    var commNum = document.getElementById("CommNum").value;
    var tradeNot = "securité: mettez les ceintures !";
    
    // get the departure airport oaci
    var depAptList = document.getElementById("depApt");
    var oaciDep = depAptList.options[depAptList.selectedIndex].value;
    
    // get the arrival airport oaci
    var arrAptList = document.getElementById("arrApt");
    var oaciArr = arrAptList.options[arrAptList.selectedIndex].value;

    // get the plane id
    var planesList = document.getElementById("selectAirplane");
    var plane = planesList.options[planesList.selectedIndex].value;
    
    // get the pilot;
    var pilotList = document.getElementById("selectPilot");
    var pilot = pilotList.options[pilotList.selectedIndex].text;
    
    // get the copilot
    var copilotList = document.getElementById("selectCopilot");
    var copilot = copilotList.options[copilotList.selectedIndex].text;
    
    // get the staff crew 
    var staffForm = document.forms.scheduledFlight;
    var staffVal = [];
    var x = 0;
	 
    for (x=0;x<staffForm.staff.length;x++)
    {
		if(staffForm.staff[x].selected)
		{
		    var string=staffForm.staff[x].value;
		    staffVal.push(string);
		}
    }
	
    $.ajax({
	contentType: "application/json",
	url: url,
	data: JSON.stringify({
	    "departure": departure, "oaciDeparture": oaciDep, "commercialNumber": commNum,
	    "oaciDestination": oaciArr,"arrivalTime": arrival, "atc": atc, "ofp": "", 
	    "notam": "notam.txt", "meteo": "meteo.pdf", "tradeNotice": tradeNot,
	    "crew": { "loginPilot":pilot,"loginCopilot":copilot,"loginHostStaff": staffVal},
	    "idAirplane":plane}),
	type: "PUT",
	processData: false
    }).done(function() {
		window.location.href = "list.html";
		
    }).fail(function() {
		alert( "Une erreur est survenue. Merci de revérifier les informations rentrées." );
    });
    
    /*{"departure": "2015-02-10", "oaciDeparture": "OIADAA", "commercialNumber": "AABB",
      "oaciDestination": "OOA","arrivalTime": "2015-02-10", "atc": "atc", "ofp": "ofp", 
      "notam": "notam.pdf", "meteo": "meteo.jpg", "tradeNotice": "text", "crew": { "pilot":"marci",
      "copilot":"marce","loginHostStaff":["marcu","marca"]}, "idAirplane":"0"}*/
}

function getServerData(url,success){
	$.ajax({
	    dataType:"json",
	    url:url,
	    type:"POST"
	}).done(success);
}

function callGetAirportDone(result){
	$("body").show();
	var airportList = $.parseJSON(JSON.stringify(result));
	populateSelect('depApt', airportList);
	populateSelect('arrApt', airportList);
	getAirplaneData("ws/airplanes",callGetAirplaneDone);
	getPiloteData("ws/pilot", callGetPilotDone);
	getCopiloteData("ws/copilot", callGetCopilotDone);
	getHostStaffData("ws/hoststaff", callGetStaffDone);
}

function callGetPilotDone(result){
	var list = $.parseJSON(JSON.stringify(result));
	populateSelect('selectPilot', list);
}

function callGetCopilotDone(result){
	var list = $.parseJSON(JSON.stringify(result));
	populateSelect('selectCopilot', list);
}

function callGetStaffDone(result){
	var list = $.parseJSON(JSON.stringify(result));
	populateSelect('selectStaff', list);
	setTimeout(function() {
		// Init the chosen list
	 	$('.chosen-select-simple').chosen();
		$('.chosen-select').chosen({max_selected_options: 7});
	}, 500); 
	
}
    
function getAirportData(url,success){
	$.ajax({
	    dataType:"json",
		url:url,
	    type:"POST",
	}).done(success)
	.fail(function(xhr){
		console.log("Erreur requête.");
		if(xhr.status==401)
			location.href = "/";
	});
}

function getPiloteData(url,success){
	$.ajax({
	    dataType:"json",
		url:url,
	    type:"POST",
	}).done(success)
	.fail(function(xhr){
		console.log("Erreur requête.");
		if(xhr.status==401)
			location.href = "/";
	});
}

function getCopiloteData(url,success){
	$.ajax({
	    dataType:"json",
		url:url,
	    type:"POST",
	}).done(success)
	.fail(function(xhr){
		console.log("Erreur requête.");
		if(xhr.status==401)
			location.href = "/";
	});
}

function getHostStaffData(url,success){
	$.ajax({
	    dataType:"json",
		url:url,
	    type:"POST",
	}).done(success)
	.fail(function(xhr){
		console.log("Erreur requête.");
		if(xhr.status==401)
			location.href = "/";
	});
}

function callGetAirplaneDone(result){
	var airplaneList = $.parseJSON(JSON.stringify(result));
	populateSelect('selectAirplane', airplaneList);
}
    
function getAirplaneData(url,success){
	$.ajax({
	    dataType:"json",
	url:url,
	    type:"POST"
	}).done(success);
}

$(function(){

	var myRole = sessionStorage.getItem('myRole');
	if(myRole=='Member')
   		window.location.href = "list.html";

	$("body").hide();
	getAirportData("ws/airports",callGetAirportDone);

	$('.form').find('input, select').on('keyup blur focus click focusout', function (e) {
	  
	  var $this = $(this),
	      label = $this.prev('label');

		  if (e.type === 'keyup') {
				if ($this.val() === '') {
		  label.removeClass('active highlight');
		} else {
		  label.addClass('active highlight');
		}
	    } else if (e.type === 'blur') {
	    	if( $this.val() === '' ) {
	    		label.removeClass('active highlight'); 
				} else {
			    label.removeClass('highlight');   
				}   
	    } else if (e.type === 'focus') {
	      
	      if( $this.val() === '' ) {
	    		label.removeClass('highlight'); 
				} 
	      else if( $this.val() !== '' ) {
			    label.addClass('highlight');
				}
	    } else if (e.type === 'click') {

	      if( $this.val() === '' ) {
	    		label.removeClass('highlight'); 
				} 
	      else if( $this.val() !== '' ) {
			    label.addClass('active');
				}
	    } else if( e.type === 'focusout'){
	    	if($this.val() !== '')
	    		$this.css("box-shadow",'0 0 0 0 black');
	    }

	});

	$('.tab a').on('click', function (e) {
	  
	  e.preventDefault();
	  
	  $(this).parent().addClass('active');
	  $(this).parent().siblings().removeClass('active');
	  
	  target = $(this).attr('href');

	  $('.tab-content > div').not(target).hide();
	  
	  $(target).fadeIn(600);
	  
	});

	// remove the box-shadow when fields not empty enymore
	$('.form').find('div div div').on('focusout', function(){
		if($(this).find('div').attr('id')==='depApt_chosen' && document.getElementById("depApt").value!=='' )
			$(this).find('div').css("box-shadow","0 0 0 0 black");
		if($(this).find('div').attr('id')==='arrApt_chosen' && document.getElementById("arrApt").value!=='' )
			$(this).find('div').css("box-shadow","0 0 0 0 black");
	});


	// Init the datepickers
	var nowTemp = new Date();
	var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), nowTemp.getHours()+4, nowTemp.getMinutes(), 0, 0);
	$('#plndDep').datetimepicker({
	
		format: 'YYYY-MM-DD HH:mm',
		minDate: now
	});
	var now2 = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), nowTemp.getHours()+5, nowTemp.getMinutes(), 0, 0);
	$('#plndArr').datetimepicker({
	
		format: 'YYYY-MM-DD HH:mm',
	    minDate: now2
	});

	
    $(".button").click(function(event){
    	if(document.getElementById("CommNum").value=='' || document.getElementById("atc").value=='' ||
		document.getElementById("depApt").value=='' || document.getElementById("arrApt").value=='' ||
		document.getElementById("plndDep").value=='' || document.getElementById("plndArr").value=='')
			{
				// if click on optional's btn and fields are empty, get back to required
				var tabOptional = $("#tabOptional"); 
				if(tabOptional.hasClass("active"))
				{
					$("#tabOptional").removeClass('active');
					$("#tabOptional").siblings().addClass('active');

					target = $("#tabOptional").siblings().find('a').attr('href');
					$('.tab-content > div').not(target).hide();
					$(target).fadeIn(600);
				}
				// if fields empty, add box-shadow; if not, remove it
				if(document.getElementById("CommNum").value=='')
					$("#CommNum").css("box-shadow","0 0 1px 1px red");
				else
					$("#CommNum").css("box-shadow","0 0 0 0 black");
				if(document.getElementById("atc").value=='')
					$("#atc").css("box-shadow","0 0 1px 1px red");
				else
					$("#atc").css("box-shadow","0 0 0 0 black");
				if(document.getElementById("depApt").value=='')
					$("#depApt_chosen").css("box-shadow","0 0 1px 1px red");
				else
					$("#depApt_chosen").css("box-shadow","0 0 0 0 black");
				if(document.getElementById("arrApt").value=='')
					$("#arrApt_chosen").css("box-shadow","0 0 1px 1px red");
				else
					$("#arrApt_chosen").css("box-shadow","0 0 0 0 black");
			}
		else{
			//event.preventDefault();
			putServerData("ws/flights");
		}
			
    });

    $('#menu ul li').click(function(evt){ // Init Click funtion on Menu
      evt.preventDefault();
      var clicked_ref = $(this).find('a').attr('href'); // Strore Href value
      var clicked_a = $(this).find('a');

      switch(clicked_ref)
      {
      	case 'list.html':
      		window.location.href = "list.html";
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
