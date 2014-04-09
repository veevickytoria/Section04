function ConvertFormToJSON(form){
	var array = jQuery(form).serializeArray();
	var json = {};

	jQuery.each(array, function() {
		json[this.name] = this.value || '';
	});

	return json;
}

$(window).load(function() { 
	// check if user is already logged in
	if(getCookie('userID')){
		window.location.href = '/home_page/index';
	}

	$('#jsNeeded').show();

	jQuery('#loginForm').bind('submit', function(event){
		event.preventDefault();

		var form = this;
		var json = new ConvertFormToJSON(form);
		json.password = SHA256(json.password);

		$.ajax({
			type: 'POST',
			url: 'http://csse371-04.csse.rose-hulman.edu/User/Login/',
			data: JSON.stringify(json),
			dataType: 'json',
			success:function(data){
				if(data.userID != null){
					document.getElementById('invalid').style.display = "none";
					setCookie('userID', data.userID,'7');
					window.location.href = "/home_page/index";
				}else{
					document.getElementById('invalid').style.display = "inline";
				}
			}
		});

		return true;
	});

	jQuery('#registerForm').bind('submit', function(event){
		event.preventDefault();


		var invalid = validateRegister();
		if (!invalid){
			var emailValid = validateEmail();
			var phoneValid = validatePhone();
			if (emailValid && phoneValid){
				var form = this;
				var json = new ConvertFormToJSON(form);
				json.password = SHA256(json.password);

				$.ajax({
					type: 'POST',
					url: 'http://csse371-04.csse.rose-hulman.edu/User/',
					data: JSON.stringify(json),
					dataType: 'json',
					success:function(data){
						if(data.userID != null){
							setCookie('userID', data.userID,'7');
							window.location.href = "/home_page/index";
						}
					}
				});
			}
		}

		return true;
	});
});

function passCheck(){
	var pass0 = document.getElementById('p0').value();
	var pass1 = document.getElementById('p1').value();
	if(pass0 != pass1)
		document.getElementById('p1').style.box-shadow('0 0 5px 4px red');
	else
		document.getElementById('p1').style.box-shadow('none');
}

function validateRegister(){
	var invalid = false;

	if(document.getElementById("regName").value == ""){
		document.getElementById("nameR").style.display = "inline";
    	invalid = true;
    }else{
    	document.getElementById("nameR").style.display = "none";
    }

	if(document.getElementById("regEmail").value == ""){
		document.getElementById("emailR").style.display = "inline";
    	invalid = true;
    }else{
    	document.getElementById("emailR").style.display = "none";
    }

	if(document.getElementById("p0").value == ""){
		document.getElementById("p0R").style.display = "inline";
    	invalid = true;
    }else{
    	document.getElementById("p0R").style.display = "none";
    }

	if(document.getElementById("p1").value == ""){
		document.getElementById("p1R").style.display = "inline";
    	invalid = true;
    }else{
    	document.getElementById("p1R").style.display = "none";
    }

	if(document.getElementById("phone").value == ""){
		document.getElementById("phoneR").style.display = "inline";
    	invalid = true;
    }else{
    	document.getElementById("phoneR").style.display = "none";
    }

	if(document.getElementById("company").value == ""){
		document.getElementById("companyR").style.display = "inline";
    	invalid = true;
    }else{
    	document.getElementById("companyR").style.display = "none";
    }

	if(document.getElementById("title").value == ""){
		document.getElementById("titleR").style.display = "inline";
    	invalid = true;
    }else{
    	document.getElementById("titleR").style.display = "none";
    }

	if(document.getElementById("location").value == ""){
		document.getElementById("locationR").style.display = "inline";
    	invalid = true;
    }else{
    	document.getElementById("locationR").style.display = "none";
    }


    return invalid;
}

function validateEmail(){
    var x= document.getElementById("regEmail").value;
    var atpos=x.indexOf("@");
    var dotpos=x.lastIndexOf(".");
    if (atpos<1 || dotpos<atpos+2 || dotpos+2>=x.length)
    {
      document.getElementById("emailV").style.display = "inline";
      return false;
    }else{
      document.getElementById("emailV").style.display = "none";
      return true;
    }
}

function validatePhone(){  
	var x= document.getElementById("phone").value;
	var phoneno = /^\(?([0-9]{3})\)?[-. ]?([0-9]{3})[-. ]?([0-9]{4})$/;  
	if(x.match(phoneno))  
	{  
	  document.getElementById("phoneV").style.display = "none";
	  return true;  
	}  
	else  
	{  
	  document.getElementById("phoneV").style.display = "inline"; 
	  return false;  
	}  
}