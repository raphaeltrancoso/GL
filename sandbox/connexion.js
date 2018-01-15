var post = function(route, donnees, fonctionRetour){
    $.post('http://localhost:8080' + route, donnees, fonctionRetour, 'json');
}

$('#bonjour').each(function(){
    $(this).animate({left: '60%'}, 10000);
});

$('#id, #mdp').keyup(function(){
    if($('#id').val() && $('#mdp').val())
	$('#envoyer').removeAttr('disabled');
});

$('#envoyer').click(function(event){
    event.preventDefault();
    post('/connexion', {login:$('#id').val(), mdp:$('#mdp').val()}, function(retour){
	if(retour.page)
	    document.location.href = retour.page;
	else
	    $('#erreur').replaceWith('<span id="erreur">' + retour.erreur + '</span>');
    });
});

$('#inscrire').click(function(){
    $(this).remove();
    $('#formulaire').show('slow');
});

var nomComplet = function(){
    if($('input[name=groupe]:checked').val() && $('#nom').val() && $('#prenom').val())
	$('#complet').val($('input[name=groupe]:checked').val()
			  + " " + $('#nom').val()
			  + " " + $('#prenom').val());
};

$('#radio1, #radio2').change(nomComplet);

$('#nom, #prenom').keyup(nomComplet);

//$(document).ready(function(){
$('#afficher').change(function(){
    if($('#mdp2').attr('type') == 'password'){
	$('#mdp2').prop('type', 'text').addClass('bleu');
	$('#mdp3').prop('type', 'text').addClass('bleu');
    }
    else{
	$('#mdp2').prop('type', 'password').removeClass('bleu');
	$('#mdp3').prop('type', 'password').removeClass('bleu');
    }
});
//});

var detectionVide = function(){
    var formulaire = ['#nom', '#prenom', '#login', '#mdp2'];

    for(var i = 0; i < formulaire.length; i++){
	var input = $(formulaire[i]);
	if(input.val())
	    input.parent().removeClass('rouge');
	else// if(!input.parent().hasClass('rouge'))
	    input.parent().addClass('rouge');
    }
};

var detectionCocher = function(){
    if($('input[name=groupe]:checked').val())
	$('#radio1').parent().removeClass('rouge');
    else// if(!$('#radio1').parent().hasClass('rouge'))
	$('#radio1').parent().addClass('rouge');
}

$('#radio1, #radio2').change(detectionCocher);

$('#nom, #prenom, #login, #mdp2').keyup(detectionVide);

$('#reinitialiser').click(function(){
    $(':input', '#formulaire')
	.not(':button, :submit, :reset, :hidden, :radio')
	.val('')
	.removeAttr('checked')
	.removeAttr('selected');
    $(':input', '#formulaire').removeAttr('checked');
    $('#mdp2').prop('type', 'password').removeClass('bleu');
    $('#mdp3').prop('type', 'password').removeClass('bleu');
    detectionVide();
    detectionCocher();
    $('#envoyer2').attr('disabled', 'disabled');
});

$('#mdp3').keyup(function(){
    if(!$('#mdp3').val() || $('#mdp2').val() != $('#mdp3').val())
	$('#mdp3').parent().addClass('violet');
    else// if($('#mdp3').parent().hasClass('violet'))
	$('#mdp3').parent().removeClass('violet');
});

$('#nom, #prenom, #login, #mdp2, #mdp3').keyup(function(){
    var formulaire = ['nom', 'prenom', 'login', 'mdp2', 'mdp3'];
    var estVide = false;

    for(var i = 0; i < formulaire.length; i++){
	if(!$('#' + formulaire[i]).val()){
	    estVide = true;
	    break;
	}
    }
    if(!estVide
       && $('#mdp2').val() == $('#mdp3').val()
       && $('input[name=groupe]:checked').val())
	$('#envoyer2').removeAttr('disabled');
    else
	$('#envoyer2').attr('disabled', 'disabled');
});

$('#envoyer2').click(function(event){
    event.preventDefault();
    var date = new Date;

    post('/inscription', {
	complet: $('#complet').val(),
	login: $('#login').val(),
	mdp: $('#mdp2').val(),
	'date': date.getTime()}, function(retour){
	    if(retour.page)
		document.location.href = retour.page;
	    else
		$('#erreur2').replaceWith('<span id="erreur2">' + retour.erreur + '</span>');
    });
});
