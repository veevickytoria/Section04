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
					setCookie('userID', data.userID,'7');
					window.location.href = "/home_page/index";
				}
			}
		});

		return true;
	});

	jQuery('#registerForm').bind('submit', function(event){
		event.preventDefault();

		var regName = document.getElementById('regName');
		var regEmail = document.getElementById('regEmail');
		var pass = document.getElementById('p0');

		if(regName.value == "" || regEmail.value == "" || pass.value == ""){
			alert("You did not enter the required fields.");
			window.location.href = "/";
		}

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