function submitCreateTaskModal(){
		$('#sCrTsk').addClass('disabled');
    	$('#cCrTsk').addClass('disabled');
		//grab the form from the html document
		var form = document.getElementById('createTask');
		//still testing validation
		//if(validateMeetingForm(form)){
		//parse it into a json object
		var formJSON = new convertFormToJSON(form);

		//manually grab the values for each field expected by the backend
		var userID = getCookie('userID');
		var postTitle = formJSON.title;
		var postisCompleted = formJSON.isCompleted;
		var postDescription = formJSON.description;
		var postDeadline = formJSON.deadline;
		var postDateCreated = formJSON.dateCreated;
		var postDateAssigned = formJSON.dateAssigned;
		var postCompletionCriteria = formJSON.completionCriteria;
		var postAssignedFrom = getCookie('userID');
		var postCreatedBy = getCookie('userID');


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
			"assignedTo":userID,
			"assignedFrom":postAssignedFrom,
			"createdBy":postCreatedBy
		});
			
		//make the POST request to the backend
		$.ajax({
			type: 'POST',
			url: 'http://csse371-04.csse.rose-hulman.edu/Task/',
			data: postData,
			success:function(data){
				//if successful then close the modal and reload the page
				$('#newTaskModal').modal('hide');
				window.location.reload(true);
			}
		});
}