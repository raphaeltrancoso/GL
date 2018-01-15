$(document).ready(function() {
    var $login = $(".login"),
		$app = $(".app");

	function post(object){
		return $.ajax({
	    	contentType: "application/json",
	    	url: object.path,
	    	data: JSON.stringify(object.data),
	    	type: "POST",
	    	success: object.success,
		    error: object.error
		})
	}

	function getRandomSalt(){
		var min = 0;
		var max = Math.pow(2, 32);
  		return Math.floor(Math.random() * (max - min)) + min
	}

    function connectUSer(saltServer, login, password){
		// Génération d'un sel client
		var saltClient = getRandomSalt();

		// Format d'envoie : [md5(md5(password) + saltServer + saltClient) + saltClient]
		var passwd = "" + $.md5("" + password + saltServer + saltClient) + saltClient;
		var user = { login: login, password: passwd };

		post({
			path: "ws/",
			data: user,
			success: function(returningData, txt, xhr){
				if(xhr.status==210)
					sessionStorage.setItem('myRole', 'CCO');
				else if(xhr.status==211)
					sessionStorage.setItem('myRole', 'Member');
	    		window.location.href = "list.html"
			},
			error: function(){
	    		$('.error').empty().append("Login error. Please try again.");
	   			document.getElementsByTagName("input")[1].value = ''
			}
		});
    }

	function requestLogin(e){
		e.preventDefault();
    	// Récupération des informations contenues dans les "input"
		var login = document.getElementsByTagName("input")[0].value;
		// Hachage du mot de passe grâce au plugin JavaScript MD5
		var password = $.md5(document.getElementsByTagName("input")[1].value);

		// Récupération du sel serveur à partir de l'identifiant utilisateur
		// et appel de [connectUser(saltServer, login, password)].
		post({
			path: "ws/login",
			data:{ login: login },
			success: function(returningData){
				connectUSer(returningData, login, password);
			},
			error: function(error){
				var error;
				if(error.status == 500){
					error = "Login error. Please try again.";
					document.getElementsByTagName("input")[0].value = '';
					document.getElementsByTagName("input")[1].value = '';
				}
				else
					error = "No response from server.";
				$('.error').empty().append(error);
			}
		});
	}

    $(document).on("click", ".login__submit", function(e){
 		requestLogin(e);
    });

   $(document).keypress(function(e){
   		// Bouton entrer
   		if(e.which == 13)
			requestLogin(e);
	});
});