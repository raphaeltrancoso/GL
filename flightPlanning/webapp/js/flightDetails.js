$(function(){ 
  var airplaneList, airplaneID;

  function toValidDate(ms){
      	var val = ms;
      	if (typeof ms === 'string')
      		val = parseInt(ms);
      	var d = new Date(val);
      	return d.toUTCString();
  }

  function replaceSpaceByPlus(str){
  	if(typeof str === 'string')
  		return str.split(' ').join('+');
  }

  function getAirplaneType(id){
    for(var i = 0; i<airplaneList.length; i++){
      if(airplaneList[i].id == id){
        $('#type').append(airplaneList[i].type);
        if(airplaneList[i].type=='Airbus A320')
        {
          $('#imgPlane').append('<img src="img/airbus-a320.jpg" alt="airbus A320">');
        }
        else if(airplaneList[i].type=='Airbus A380')
        {
          $('#imgPlane').append('<img src="img/airbus-a380.png" alt="airbus A320">');
        }
        else if(airplaneList[i].type=='Boeing 487')
        {
          $('#imgPlane').append('<img src="img/boeing-787.jpg" alt="airbus A320">');
        }
        else if(airplaneList[i].type=='Boeing 737')
        {
          $('#imgPlane').append('<img src="img/boeing-737.png" alt="airbus A320">');
        }
        else
          $('#imgPlane').append('<img src="img/flight.jpg" alt="flightImage">');
        $('#imgPlane').append('<div class="clear"></div>');
        $('#capa').append(airplaneList[i].capacity+" seats") ;
        $('#weight').append(airplaneList[i].weight+" tonnes");
      }
    }
  }

  function getAirplaneData(url,success){
      $.ajax({
        dataType:"json",
        url:url,
        type:"POST"
      }).done(success);
  }

  function callGetAirplaneDone(result){
    airplaneList = $.parseJSON(JSON.stringify(result));
    getFlightData(url,getFlightDetails);
  }

  function getFlightData(url, success){
    $.ajax({
      dataType:"json",
      url:url,
      type:"GET"
    }).done(success);
  }

  function getFlightDetails(data){
    var chaine = JSON.stringify(data);
    var flight; 
    if (chaine == undefined || chaine == null){
      flight={ commercialNumber : "bonjour" };
    }else {
      flight=$.parseJSON(chaine);
    }	
    
    airplaneID=flight.idAirplane;
    getAirplaneType(airplaneID);

    $("#commNumber").append(flight.commercialNumber);
    $("#atc").append(flight.atc);
    $("#deptAirport").append(flight.oaciDeparture);
    $('#arrAirport').append(flight.oaciDestination);
    $('#deptTime').append(toValidDate(flight.departure));
    $('#arrTime').append(toValidDate(flight.arrivalTime));
    $('#pilot').append(flight.crew.loginPilot);
    $('#copilot').append(flight.crew.loginCopilot);
    for(var i = 0; i < flight.crew.loginHostStaff.length; i++ )
      {
        if(flight.crew.loginHostStaff[i]!='')
        {
          if(i+1==flight.crew.loginHostStaff.length)
            $('#staff').append(flight.crew.loginHostStaff[i]);
          else
            $('#staff').append(flight.crew.loginHostStaff[i]+" ");
        }
      }

  }

  /* Get details from selected flight*/
	var id = JSON.parse(sessionStorage.myID);
  var idpart = $.parseJSON(JSON.stringify(id));
  var oaci = replaceSpaceByPlus(idpart.oaciDeparture);
  var commercial = replaceSpaceByPlus(idpart.commercialNumber);
  var url = "ws/flights/"+idpart.departure+"_"+oaci+"_"+commercial;
  getAirplaneData("ws/airplanes",callGetAirplaneDone);

  // remove last border-bottom from list in tab conten
  $('.inside ul li:last-child').css('border-bottom','0px')
  
  // Add .selected class to first tab on load
  $('.tabs').each(function(){
    $(this).children('li').children('a:first').addClass('selected');
  });

  $('.inside > *').hide();
  $('.inside > *:first-child').show();
  
  $('.tabs li a').click(function(evt){ // Init Click funtion on Tabs
    var clicked_tab_ref = $(this).attr('href'); // Strore Href value
    $(this).parent().parent().children('li').children('a').removeClass('selected'); //Remove selected from all tabs
    $(this).addClass('selected');
    $(this).parent().parent().parent().children('.inside').children('*').hide();
    $('.inside ' + clicked_tab_ref).fadeIn(500);
      evt.preventDefault();
  })
});