function submitCreateTaskModal(){
	alert("tasks js")
	$('#sCrTsk').addClass('disabled');
	$('#cCrTsk').addClass('disabled');
	//grab the form from the html document
	var form = document.getElementById('createTask');
	//still testing validation
	//if(validateMeetingForm(form)){
	//parse it into a json object
	var formJSON = new convertFormToJSON(form);
	var invalid = validateNewValues(formJSON);
	if (!invalid){
		//manually grab the values for each field expected by the backend
		var userID = getCookie('userID');
		var postTitle = formJSON.title;
		var postisCompleted = false;
		var postDescription = formJSON.description;
		
		var date = formJSON.deadlinedate.split("-");
		var time = formJSON.deadlinetime.split(":");
		datetime = new Date(date[0], date[1]-1, date[2], time[0], time[1]);
		datetime = datetime.getTime()/1000.0;
		var postDeadline = datetime;

		var dt = formJSON.dateCreated.split(" ");
		date = dt[0].split("-");
		time = dt[1].split(":");
		datetime = new Date(date[0], date[1]-1, date[2], time[0], time[1]);
		datetime = datetime.getTime()/1000.0;
		var postDateCreated = datetime;

		dt = formJSON.dateAssigned.split(" ");
		date = dt[0].split("-");
		time = dt[1].split(":");
		datetime = new Date(date[0], date[1]-1, date[2], time[0], time[1]);
		datetime = datetime.getTime()/1000.0;
		var postDateAssigned = datetime;

		var postCompletionCriteria = formJSON.completionCriteria;
		var postAssignedFrom = getCookie('userID');
		var postCreatedBy = getCookie('userID');
		var postAssignedTo = formJSON.assignedTo;

		var attendees = getMembers('assignedTo');
		var postAttendance = [];

		for (var i = attendees.length - 1; i >= 0; i--) {
		  postAttendance.push({"userID":attendees[i]});
		};

		//set up the data for the call
		var postData = JSON.stringify({
			"title":postTitle,
			"isCompleted":postisCompleted,
			"description":postDescription,
			"deadline":postDeadline,
			"dateCreated":postDateCreated,
			"dateAssigned":postDateAssigned,
			"completionCriteria":postCompletionCriteria,
			"assignedTo":postAssignedTo,
			"assignedFrom":postAssignedFrom,
			"createdBy":postCreatedBy
		});
			
		//make the POST request to the backend
		$.ajax({
			type: 'POST',
			url: 'http://csse371-04.csse.rose-hulman.edu/Task/',
			data: postData,
			success:function(data){
				// if successful then close the modal and reload the page
				$('#newTaskModal').modal('hide');
				alert("Task successfully created! Reloading page...")
				window.location.reload(true);
			}
		});
	}
}


function validateNewValues(JSONForm){
	var invalidFields = false;

	for (key in JSONForm){
		if(document.getElementById(key+"R") != null){
			if(JSONForm[key] == ""){
				invalidFields = true;
				document.getElementById(key+"R").style.display = "inline";
			}
			else{
				document.getElementById(key+"R").style.display = "none";
			}
		}
	}

	var additionalkeys = ["deadlinetime","deadlinedate"]
	for (key in additionalkeys){
		if(document.getElementById(additionalkeys[key]) != null && document.getElementById(additionalkeys[key]+"R") != null){
			if(document.getElementById(additionalkeys[key]).value == ""){
				invalidFields = true;
				document.getElementById(additionalkeys[key]+"R").style.display = "inline";
			}
			else{
				document.getElementById(additionalkeys[key]+"R").style.display = "none";
			}
		}
	}

	return invalidFields
}