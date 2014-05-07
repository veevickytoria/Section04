// NewMeeting Modal
function NewMeetingModal(documentID, blankID){
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
		var text = document.createTextNode("Create Meeting");
		header.appendChild(text);
		var doc = document.getElementById("header");
		doc.appendChild(header);
	}

	newModal.createBody = function(users){
		parentAddText.call(this, "* Indicate required fields.","","", "body");
		parentAddBreak.call(this,"body");
		parentAddText.call(this, "Topic*:","","", "body");
		parentAddElement.call(this, "text", "", "title", "title", "form-control", "body"); // type, value, id, name, class, location
		parentAddText.call(this, "Required", "titleR", "required", "body");
		parentAddBreak.call(this, "body");

		parentAddText.call(this, "Location:", "", "", "body");
		parentAddElement.call(this, "text", "", "location", "location", "form-control", "body");
		parentAddText.call(this, "Required", "locationR", "required", "body");
		parentAddBreak.call(this, "body");

		parentAddText.call(this, "Start Date:", "", "", "body");
		parentAddElement.call(this, "date", "", "dateStart", "dateStart", "form-control formDate", "body");
		parentAddText.call(this, "Required", "dateStartR", "required", "body");

		parentAddText.call(this, "Start Time:", "", "", "body");
		parentAddElement.call(this, "time", "", "timeStart", "timeStart", "form-control formTime", "body");
		parentAddText.call(this, "Required", "timeStartR", "required", "body");
		
		parentAddText.call(this, "End Date:", "", "", "body");
		parentAddElement.call(this, "date", "", "dateEnd", "dateEnd", "form-control formDate", "body"); // fix new lines in add element function for ones that don't need them
		parentAddText.call(this, "Required", "dateEndR", "required", "body");

		parentAddText.call(this, "End Time:", "", "", "body");
		parentAddElement.call(this, "time", "", "timeEnd", "timeEnd", "form-control formTime", "body");
		parentAddText.call(this, "Required", "timeEndR", "required", "body");

		parentAddText.call(this, "Description:", "", "", "body");
		parentAddElement.call(this, "textarea", "", "description", "description", "form-control", "body");
		parentAddText.call(this, "Required", "descriptionR", "required", "body");
		parentAddBreak.call(this, "body");

		parentAddText.call(this, "Attendees*","","", "body");
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
			
			var postUid = getCookie('userID');
			var postTitle = form.title;
			var members = newModal.getMembers('members');

			var postMembers = [{"userID":postUid}];
			for (var i = members.length - 1; i >= 0; i--) {
				postMembers.push({"userID":members[i]});
			}
			var splitTime = form.timeStart.split(":");
			var splitDate = form.dateStart.split("-");
			var startDateTime = new Date(splitDate[0], splitDate[1]-1, splitDate[2], splitTime[0], splitTime[1]);
			startDateTime = startDateTime.getTime()/1000.0;
			var postDatetime = startDateTime;

			splitTime = form.timeEnd.split(":");
			splitDate = form.dateEnd.split("-");
			var endDateTime = new Date(splitDate[0], splitDate[1]-1, splitDate[2], splitTime[0], splitTime[1]);
			endDateTime = endDateTime.getTime()/1000.0;
			var postEndDatetime = endDateTime;
			var postDescription = form.description;
			var postLocation = form.location;

			var postData = JSON.stringify({
				"userID":postUid,
				"title":postTitle,
				"location":postLocation,
				"datetime":postDatetime,
				"endDatetime":postEndDatetime,
				"description":postDescription,
				"attendance":postMembers
			});



			$.ajax({
				type: 'POST',
				url: 'http://csse371-04.csse.rose-hulman.edu/Meeting/',
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

	return newModal}



// ViewMeeting Modal
function ViewMeetingModal(documentID, meetingID){
	var viewModal = NinjaModal(documentID, meetingID);
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
		var text = document.createTextNode("View Meeting Details");
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
			url: 'http://csse371-04.csse.rose-hulman.edu/Meeting/' + meetingID,
			success:function(data){
				meetingArray = JSON.parse(data);
			},
			async: false
		});

		for (i in meetingArray['members']){
			$.ajax({
				type: 'GET',
				url: 'http://csse371-04.csse.rose-hulman.edu/User/' + meetingArray['members'][i]['userID'],
				success:function(data){
					membInfo = JSON.parse(data);
					membs.push({'name':membInfo['name']});
				},
				async: false
			});
		}
	

		var table = document.createElement("table");
		table.setAttribute("id", "members");
		table.setAttribute("class", "viewData");


		tableMems = parentPopulateTableRows.call(this, membs, "name", table);
		var doc = document.getElementById("body");
		doc.appendChild(tableMems);

		// Get meeting Info
		var membInfo;
		var membs = new Array();

		$.ajax({
			type: 'GET',
			url: 'http://csse371-04.csse.rose-hulman.edu/Meeting/' + meetingID,
			success:function(data){
				meetingArray = JSON.parse(data);
			},
			async: false
		});

		for (i in meetingArray['attendance']){
			$.ajax({
				type: 'GET',
				url: 'http://csse371-04.csse.rose-hulman.edu/User/' + meetingArray['attendance'][i]['userID'],
				success:function(data){
					membInfo = JSON.parse(data);
					membs.push({'name':membInfo['name']});
				},
				async: false
			});
		}

		//add meeting info
		parentAddText.call(this, "Topic:","","viewLabel", "body");
		parentAddText.call(this, meetingArray["title"], "title", "viewData", "body");
		parentAddBreak.call(this, "body");

		parentAddText.call(this, "Location:", "", "viewLabel", "body");
		parentAddText.call(this, meetingArray["location"],"location","viewData", "body");
		parentAddBreak.call(this, "body");

		//fix get time for parsing

		parentAddText.call(this, "Start Time:","","viewLabel", "body");
		var date = epochToDate(meetingArray["datetime"]);
		var time = epochToTime(meetingArray["datetime"]);
		parentAddText.call(this, date,"datetime","viewData", "body");
		parentAddText.call(this, time,"datetime","viewData", "body");
		parentAddBreak.call(this, "body");

		parentAddText.call(this, "End Time:","","viewLabel", "body");
		date = epochToDate(meetingArray["endDatetime"]);
		time = epochToTime(meetingArray["endDatetime"]);
		parentAddText.call(this, date,"endDatetime","viewData", "body");
		parentAddText.call(this, time,"endDatetime","viewData", "body");
		parentAddBreak.call(this, "body");


		parentAddText.call(this, "Description:", "", "viewLabel", "body");
		parentAddText.call(this, meetingArray["description"],"description","viewData", "body");
		parentAddBreak.call(this, "body");

		parentAddText.call(this, "Members: ","","viewLabel", "body");

		var table = document.createElement("table");
		table.setAttribute("id", "attendance");
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
		var modal = ModalFactory.createModal(EditMeetingModal, "MeetingModal", meetingID);

  		modal.showModal(users);
	}

	return viewModal
}


function EditMeetingModal(documentID, meetingID){
	var editModal = NinjaModal(documentID, meetingID);

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
		var text = document.createTextNode("Edit Meeting");
		header.appendChild(text);
		var doc = document.getElementById("header");
		doc.appendChild(header);
	}

	editModal.createBody = function(users){
		parentAddText.call(this, "* Indicate required fields.","","", "body");
		parentAddBreak.call(this,"body");
		parentAddText.call(this, "Topic*:","","", "body");
		parentAddElement.call(this, "text", "", "title", "title", "form-control", "body"); // type, value, id, name, class, location
		parentAddText.call(this, "Required", "titleR", "required", "body");
		parentAddBreak.call(this, "body");

		parentAddText.call(this, "Location:", "", "", "body");
		parentAddElement.call(this, "text", "", "location", "location", "form-control", "body");
		parentAddText.call(this, "Required", "locationR", "required", "body");
		parentAddBreak.call(this, "body");

		parentAddText.call(this, "Start Date:", "", "", "body");
		parentAddElement.call(this, "date", "", "dateStart", "dateStart", "form-control formDate", "body");
		parentAddText.call(this, "Required", "dateStartR", "required", "body");


		parentAddText.call(this, "Start Time:", "", "", "body");
		parentAddElement.call(this, "time", "", "timeStart", "timeStart", "form-control formTime", "body");
		parentAddText.call(this, "Required", "timeStartR", "required", "body");
		
		parentAddText.call(this, "End Date:", "", "", "body");
		parentAddElement.call(this, "date", "", "dateEnd", "dateEnd", "form-control formDate", "body"); // fix new lines in add element function for ones that don't need them
		parentAddText.call(this, "Required", "dateEndR", "required", "body");

		parentAddText.call(this, "End Time:", "", "", "body");
		parentAddElement.call(this, "time", "", "timeEnd", "timeEnd", "form-control formTime", "body");
		parentAddText.call(this, "Required", "timeEndR", "required", "body");

		parentAddText.call(this, "Description:", "", "", "body");
		parentAddElement.call(this, "textarea", "", "description", "description", "form-control", "body");
		parentAddText.call(this, "Required", "descriptionR", "required", "body");
		parentAddBreak.call(this, "body");

		parentAddText.call(this, "Attendees*","","", "body");
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

		element = parentPopulateSelect.call(this, users["users"], "name", "userID", "", element);
		doc.appendChild(element);

		parentAddText.call(this, "Required", "membersR", "required", "body");
		parentAddBreak.call(this,"body");

		// Populate Data
		var meetingArray,mid;
		var currentMembs = new Array();

		$.ajax({
			type: 'GET',
			url: 'http://csse371-04.csse.rose-hulman.edu/Meeting/' + meetingID,
			success:function(data){
				meetingArray = JSON.parse(data);
			},
			async: false
		});

		for (i in meetingArray['members']){
			mid = meetingArray['members'][i]['userID'];
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

		document.getElementById("title").value = meetingArray["title"];
		document.getElementById("location").value = meetingArray["location"];
		//figure out how to do this for datetime

		var date = epochToDate(meetingArray["datetime"]);
		var time = epochToTime(meetingArray["datetime"]);

		document.getElementById("dateStart").value = date;
		document.getElementById("timeStart").value = time;

		date = epochToDate(meetingArray["endDatetime"]);
		time = epochToTime(meetingArray["endDatetime"]);

		document.getElementById("dateEnd").value = date;
		document.getElementById("timeEnd").value = time;



		document.getElementById("description").value = meetingArray["description"];

		parentPopulateSelect.call(this,[], "name", "mid", currentMembs, document.getElementById("members"));
	}

	editModal.close = function(users){
		parentClose.call(this);

		var ModalFactory = abstractModalFactory();
		var modal = ModalFactory.createModal(ViewMeetingModal, "MeetingModal", meetingID);

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
			
			var postUid = getCookie('userID');
			var postTitle = form.title;
			var members = editModal.getMembers('members');

			var postMembers = new Array();

			for (var i = members.length - 1; i >= 0; i--) {
				postMembers.push({"userID":members[i]});
			};

			var splitTime = form.timeStart.split(":");
			var splitDate = form.dateStart.split("-");
			var startDateTime = new Date(splitDate[0], splitDate[1]-1, splitDate[2], splitTime[0], splitTime[1]);
			startDateTime = startDateTime.getTime()/1000.0;
			var postDatetime = startDateTime;

			splitTime = form.timeEnd.split(":");
			splitDate = form.dateEnd.split("-");
			var endDateTime = new Date(splitDate[0], splitDate[1]-1, splitDate[2], splitTime[0], splitTime[1]);
			endDateTime = endDateTime.getTime()/1000.0;
			var postEndDatetime = endDateTime;
			var postDescription = form.description;
			var postLocation = form.location;

			var updateTitle = JSON.stringify({
				"meetingID":meetingID,
				"field":"title",
				"value":postTitle
			});

			var updateLocation = JSON.stringify({
				"meetingID":meetingID,
				"field":"location",
				"value":postLocation
			});

			var updateDatetime = JSON.stringify({
				"meetingID":meetingID,
				"field":"datetime",
				"value":postDatetime
			});

			var updateEndDatetime = JSON.stringify({
				"meetingID":meetingID,
				"field":"endDatetime",
				"value":postEndDatetime
			});

			var updateDescription = JSON.stringify({
				"meetingID":meetingID,
				"field":"description",
				"value":postDescription
			});

			var updateMembers = JSON.stringify({
				"meetingID":meetingID,
				"field":"members",
				"value":postMembers
			});

			$.ajax({
				type: 'PUT',
				url: 'http://csse371-04.csse.rose-hulman.edu/Meeting/',
				data: updateTitle,
				success:function(data){
				// do nothing
				},
				async: false
			})

			$.ajax({
				type: 'PUT',
				url: 'http://csse371-04.csse.rose-hulman.edu/Meeting/',
				data: updateLocation,
				success:function(data){
				// do nothing
				},
				async: false
			})

			$.ajax({
				type: 'PUT',
				url: 'http://csse371-04.csse.rose-hulman.edu/Meeting/',
				data: updateDatetime,
				success:function(data){
				// do nothing
				},
				async: false
			})

			$.ajax({
				type: 'PUT',
				url: 'http://csse371-04.csse.rose-hulman.edu/Meeting/',
				data: updateEndDatetime,
				success:function(data){
				// do nothing
				},
				async: false
			})

			$.ajax({
				type: 'PUT',
				url: 'http://csse371-04.csse.rose-hulman.edu/Meeting/',
				data: updateDescription,
				success:function(data){
				// do nothing
				},
				async: false
			})

			//if there is an editing error, this is probably where it is
			$.ajax({
			type: 'PUT',
				url: 'http://csse371-04.csse.rose-hulman.edu/Meeting/',
				data: updateMembers,
				success:function(data){
					parentClose.call(this);

					var ModalFactory = abstractModalFactory();
					var modal = ModalFactory.createModal(ViewMeetingModal, documentID, meetingID);

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

