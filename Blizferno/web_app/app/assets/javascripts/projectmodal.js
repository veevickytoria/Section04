// NewProject Modal
function NewProjectModal(documentID, blankID){
	var newModal = NinjaModal(documentID, blankID);

	var parentShow = newModal.showModal;
	var parentAddText = newModal.addText;
	var parentAddBreak = newModal.addBreak;
	var parentAddElement = newModal.addElement;
	var parentPopulateSelect = newModal.populateSelect;
	var parentConvertToJSON = newModal.convertFormToJSON;
	var parentHasSelected = newModal.hasSelectedValue;
	var parentClose = newModal.close;

	newModal.showModal = function(users, meetings){
		var charToReplaceWith = ":";
		var userJson = users.replace(/=>/g,charToReplaceWith);
		var usersParsed = JSON.parse(userJson);

		newModal.createHeader();
		newModal.createBody(usersParsed);
		newModal.createFooter();
		parentShow.call(this);
	}

	newModal.createHeader = function(){
		var header = document.createElement("h1");
		header.setAttribute("class", "modal-title");
		var text = document.createTextNode("Create Project");
		header.appendChild(text);
		var doc = document.getElementById("header");
		doc.appendChild(header);
	}

	newModal.createBody = function(users){
		parentAddText.call(this, "* Indicate required fields.","","", "body");
		parentAddBreak.call(this,"body");
		parentAddText.call(this, "Project Title*","","", "body");
		parentAddElement.call(this, "text", "", "title", "title", "", "body");
		parentAddText.call(this, "Required", "titleR", "required", "body");
		parentAddBreak.call(this, "body");
		

		parentAddText.call(this, "Project Members*","","", "body");
		var doc = document.getElementById("body");
		var element = document.createElement("select");
		element.setAttribute("class","form-control");
		element.setAttribute("multiple","multiple");
		element.setAttribute("size","6");
		element.setAttribute("id", "members")



		parentAddText.call(this, "Meetings*","","", "body");
		var doc = document.getElementById("body");
		var element = document.createElement("select");
		element.setAttribute("class","form-control");
		element.setAttribute("multiple","multiple");
		element.setAttribute("size","6");
		element.setAttribute("id", "members")

		var elementNames = parentPopulateSelect.call(this, users["users"], "name", "userID", [], element);
		alert(meetings);
		var elementMeetings = parentPopulateSelect.call(this, meetings["meetingID"], "title", "meetingID", [], element);


		doc.appendChild(elementNames);
		doc.appendChild(elementMeetings);

		parentAddText.call(this, "Required", "membersR", "required", "body");
		parentAddBreak.call(this,"body");
	}

	newModal.createFooter = function(){
		// Close button
		var elementClose = document.createElement("button");
		elementClose.setAttribute("class", "btn btn-primary");
		elementClose.innerHTML = "Close";
		elementClose.onclick = newModal.close;

		// ActionButton
		var elementSubmit = document.createElement("input");
		elementSubmit.setAttribute("type", "button");
		elementSubmit.onclick = newModal.executeAction;
		elementSubmit.setAttribute("class", "btn btn-primary");
		elementSubmit.value = "Submit";

		var doc = document.getElementById("footer");
		doc.appendChild(elementClose);
		doc.appendChild(elementSubmit);
	}

	newModal.close = function(){
		parentClose.call(this);
		$('#' + documentID).modal('hide');
	}

	newModal.executeAction = function(){
		var invalid = newModal.validate();
		if(!invalid){
			var form = parentConvertToJSON.call(this, document.getElementById("body"))
			
			var uid = getCookie('userID');
		    var postTitle = form.title;
		    var members = newModal.getMembers('members');

		    var postMembers = [{"userID":uid}];

		    for (var i = members.length - 1; i >= 0; i--) {
		        postMembers.push({"userID":members[i]});
		    };

		    //set up the data for the call
		    var postData = JSON.stringify({
		        "projectTitle":postTitle,
		        "members":postMembers
		    });

			$.ajax({
				type: 'POST',
				url: 'http://csse371-04.csse.rose-hulman.edu/Project/',
				data: postData,
				success:function(data){
					parentClose.call(this);
					window.location.reload(true);
				}
			});
		}
	}

	newModal.validate = function(){
		var invalidFields = false;
  
		if(document.getElementById("title") != null && document.getElementById("titleR") != null){
			if(document.getElementById("title").value == ""){
				invalidFields = true;
				document.getElementById("titleR").style.display = "inline";
			}
			else{
				document.getElementById("titleR").style.display = "none";
			}
		}

		if(document.getElementById("members") != null && document.getElementById("membersR") != null){
			var result = parentHasSelected.call(this, document.getElementById("members"));
			if(!result){
				invalidFields = true;
				document.getElementById("membersR").style.display = "inline";
			}
			else{
				document.getElementById("membersR").style.display = "none";
			}
		}

		return invalidFields
	}

	newModal.getMembers = function(id){
		var newMembers = [];
		$( '#' + id + ' :selected' ).each( function( i, selected ) {
			newMembers[i] = $( selected ).val();
		});
		return newMembers;
	}

	return newModal
}