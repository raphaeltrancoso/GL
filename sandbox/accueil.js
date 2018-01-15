var moyenne;

var post = function(route, donnees, fonctionRetour){
    $.post('http://localhost:8080' + route, donnees, fonctionRetour, 'json');
}

$('#note').click(function(){
    $('#soumettre').removeClass('cacher');
});

$('#soumettre').click(function(){
    var valeur = document.getElementById('note').value;
    post('/note', {note: valeur}, function(retour){
	if(retour.moyenne){
	    moyenne = retour.moyenne;
	    $('#moyenne').removeClass('cacher');
	}
    });
});

$('#moyenne').click(function(){
    $('#moyenne2').replaceWith('<span id="moyenne2">' + moyenne + '</span>');
});

$('#chat').click(function(event){
    event.preventDefault();
    post('/chat', {}, function(retour){
	if(retour.page)
	    document.location.href = retour.page;
    });
});
