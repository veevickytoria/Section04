
// NewGroup Modal
function NewGroupModal(documentID, blankID){
	var newModal = NinjaModal(documentID, blankID);

	var parentShow = newModal.showModal;
	var parentAddText = newModal.addText;
	var parentAddBreak = newModal.addBreak;
	var parentAddElement = newModal.addElement;
	var parentPopulateSelect = newModal.populateSelect;
	var parentConvertToJSON = newModal.convertFormToJSON;
	var parentHasSelected = newModal.hasSelectedValue;
	var parentClose = newModal.close;

	newModal.showModal = function(users){
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
		var text = document.createTextNode("Create Group");
		header.appendChild(text);
		var doc = document.getElementById("header");
		doc.appendChild(header);
	}

	newModal.createBody = function(users){
		parentAddText.call(this, "* Indicate required fields.","","", "body");
		parentAddBreak.call(this,"body");
		parentAddText.call(this, "Group Title*","","", "body");
		parentAddElement.call(this, "text", "", "title", "title", "", "body");
		parentAddText.call(this, "Required", "titleR", "required", "body");
		parentAddBreak.call(this, "body");
		
		parentAddText.call(this, "Group Members*","","", "body");
		var doc = document.getElementById("body");
		var element = document.createElement("select");
		element.setAttribute("class","form-control");
		element.setAttribute("multiple","multiple");
		element.setAttribute("size","6");
		element.setAttribute("id", "members")

		var elementNames = parentPopulateSelect.call(this, users["users"], "name", "userID", [], element);
		doc.appendChild(elementNames);

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
		        "groupTitle":postTitle,
		        "members":postMembers
		    });

			$.ajax({
				type: 'POST',
				url: 'http://csse371-04.csse.rose-hulman.edu/Group/',
				data: postData,
				success:function(data){
					parentClose.call(this);
					alert("Group successfully created! Reloading page...")
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

// EditGroup Modal
function EditGroupModal(documentID, groupID){
	var editModal = NinjaModal(documentID, groupID);

	var parentShow = editModal.showModal;
	var parentAddText = editModal.addText;
	var parentAddBreak = editModal.addBreak;
	var parentAddElement = editModal.addElement;
	var parentPopulateSelect = editModal.populateSelect;
	var parentConvertToJSON = editModal.convertFormToJSON;
	var parentHasSelected = editModal.hasSelectedValue;
	var parentClose = editModal.close;

	editModal.showModal = function(users){
		var charToReplaceWith = ":";
		var userJson = users.replace(/=>/g,charToReplaceWith);
		var usersParsed = JSON.parse(userJson);

		editModal.createHeader();
		editModal.createBody(usersParsed);
		editModal.createFooter(users);
		parentShow.call(this);
	}

	editModal.createHeader = function(){
		var header = document.createElement("h1");
		header.setAttribute("class", "modal-title");
		var text = document.createTextNode("Edit Group");
		header.appendChild(text);
		var doc = document.getElementById("header");
		doc.appendChild(header);
	}

	editModal.createBody = function(users){
		parentAddText.call(this, "* Indicate required fields.","","", "body");
		parentAddBreak.call(this,"body");
		parentAddText.call(this, "Group Title*","","", "body");
		parentAddElement.call(this, "text", "", "title", "title", "", "body");
		parentAddText.call(this, "Required", "titleR", "required", "body");
		parentAddBreak.call(this, "body");
		
		parentAddText.call(this, "Group Members*","","", "body");
		var doc = document.getElementById("body");
		var element = document.createElement("select");
		element.setAttribute("class","form-control");
		element.setAttribute("multiple","multiple");
		element.setAttribute("size","6");
		element.setAttribute("id", "members")

		element = parentPopulateSelect.call(this, users["users"], "name", "userID", "", element);
		doc.appendChild(element);

		parentAddText.call(this, "Required", "membersR", "required", "body");
		parentAddBreak.call(this,"body");

		// Populate Data
		var groupArray,mid;
		var currentMembs = new Array();

		$.ajax({
			type: 'GET',
			url: 'http://csse371-04.csse.rose-hulman.edu/Group/' + groupID,
			success:function(data){
				groupArray = JSON.parse(data);
			},
			async: false
		});

		for (i in groupArray['members']){
			mid = groupArray['members'][i]['userID'];
			$.ajax({
				type: 'GET',
				url: 'http://csse371-04.csse.rose-hulman.edu/User/' + mid,
				success:function(data){
					membInfo = JSON.parse(data);
					currentMembs.push({'mid':mid,'name':membInfo['name']});
				},
				async: false
			});
		}

		document.getElementById("title").value = groupArray["groupTitle"];

		parentPopulateSelect.call(this,[], "name", "mid", currentMembs, document.getElementById("members"));
	}

	editModal.close = function(users){
		parentClose.call(this);

		var ModalFactory = abstractModalFactory();
		var modal = ModalFactory.createModal(ViewGroupModal, "GroupModal", groupID);

  		modal.showModal(users);
	}

	editModal.createFooter = function(users){
		var self = this;

		// Close button
		var elementClose = document.createElement("button");
		elementClose.setAttribute("class", "btn btn-primary");
		elementClose.innerHTML = "Close";
		elementClose.onclick = function(){self.close(users);};

		// ActionButton
		var elementSubmit = document.createElement("input");
		elementSubmit.setAttribute("type", "button");
		elementSubmit.onclick = function(){self.executeAction(users);};
		elementSubmit.setAttribute("class", "btn btn-primary");
		elementSubmit.value = "Submit";

		var doc = document.getElementById("footer");
		doc.appendChild(elementClose);
		doc.appendChild(elementSubmit);
	}

	editModal.executeAction = function(users){
		var invalid = editModal.validate();
		if(!invalid){
			var form = parentConvertToJSON.call(this, document.getElementById("body"))
			
			var postTitle = form.title;
			var members = editModal.getMembers('members');

			var postMembers = new Array();

			for (var i = members.length - 1; i >= 0; i--) {
				postMembers.push({"userID":members[i]});
			};

			//set up the data for the call
			var updateTitle = JSON.stringify({
				"groupID":groupID,
				"field":"groupTitle",
				"value":postTitle
			});

			var updateMembers = JSON.stringify({
				"groupID":groupID,
				"field":"members",
				"value":postMembers
			});

			$.ajax({
				type: 'PUT',
				url: 'http://csse371-04.csse.rose-hulman.edu/Group/',
				data: updateTitle,
				success:function(data){
				// do nothing
				},
				async: false
			})

			$.ajax({
			type: 'PUT',
				url: 'http://csse371-04.csse.rose-hulman.edu/Group/',
				data: updateMembers,
				success:function(data){
					parentClose.call(this);

					var ModalFactory = abstractModalFactory();
					var modal = ModalFactory.createModal(ViewGroupModal, documentID, groupID);

			  		modal.showModal(users);
				}
			});
		}
	}

	editModal.validate = function(){
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

	editModal.getMembers = function(id){
		var newMembers = [];
		$( '#' + id + ' :selected' ).each( function( i, selected ) {
			newMembers[i] = $( selected ).val();
		});
		return newMembers;
	}

	return editModal
}

// ViewGroup Modal
function ViewGroupModal(documentID, groupID){
	var viewModal = NinjaModal(documentID, groupID);

	var parentShow = viewModal.showModal;
	var parentAddText = viewModal.addText;
	var parentAddBreak = viewModal.addBreak;
	var parentAddElement = viewModal.addElement;
	var parentClose = viewModal.close;
	var parentPopulateTableRows = viewModal.populateTableRows;

	viewModal.showModal = function(users){
		viewModal.createHeader();
		viewModal.createBody();
		viewModal.createFooter(users);
		parentShow.call(this);
	}

	viewModal.createHeader = function(){
		var header = document.createElement("h1");
		header.setAttribute("class", "modal-title");
		var text = document.createTextNode("View Group Details");
		header.appendChild(text);
		var doc = document.getElementById("header");
		doc.appendChild(header);
	}

	viewModal.createBody = function(){
		// Get Group Info
		var membInfo;
  		var membs = new Array();

		$.ajax({
			type: 'GET',
			url: 'http://csse371-04.csse.rose-hulman.edu/Group/' + groupID,
			success:function(data){
				groupArray = JSON.parse(data);
			},
			async: false
		});

		for (i in groupArray['members']){
			$.ajax({
				type: 'GET',
				url: 'http://csse371-04.csse.rose-hulman.edu/User/' + groupArray['members'][i]['userID'],
				success:function(data){
					membInfo = JSON.parse(data);
					membs.push({'name':membInfo['name']});
				},
				async: false
			});
		}

		parentAddText.call(this, "Group Title: ","","viewLabel", "body");
		parentAddText.call(this, groupArray["groupTitle"],"title","viewData", "body");
		parentAddText.call(this, "Members: ","","viewLabel", "body");

		var table = document.createElement("table");
		table.setAttribute("id", "members");
		table.setAttribute("class", "viewData");


		tableMems = parentPopulateTableRows.call(this, membs, "name", table);
		var doc = document.getElementById("body");
		doc.appendChild(tableMems);
	}

	viewModal.createFooter = function(users){
		// Close button
		var self = this;
		var elementClose = document.createElement("button");
		elementClose.setAttribute("class", "btn btn-primary");
		elementClose.innerHTML = "Close";
		elementClose.onclick = viewModal.close;

		// ActionButton
		var elementSubmit = document.createElement("input");
		elementSubmit.setAttribute("type", "button");
		elementSubmit.onclick = function(){self.executeAction(users);};
		elementSubmit.setAttribute("class", "btn btn-primary");
		elementSubmit.value = "Edit";

		var doc = document.getElementById("footer");
		doc.appendChild(elementClose);
		doc.appendChild(elementSubmit);
	}

	viewModal.close = function(){
		parentClose.call(this);
		$('#' + documentID).modal('hide');
	}

	viewModal.executeAction = function(users){
		parentClose.call(this);
		var ModalFactory = abstractModalFactory();
		var modal = ModalFactory.createModal(EditGroupModal, "GroupModal", groupID);

  		modal.showModal(users);
	}

	return viewModal
}