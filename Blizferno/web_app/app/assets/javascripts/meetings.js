function submitCreateMeetingModal(){
	var form = document.getElementById('createMeetingForm');
	var formJSON = new convertFormToJSON(form);

	var postId = getCookie('userID');
	var postTitle = formJSON.title;
	var postLocation = formJSON.location;
	var postDatetime = formJSON.dateStart + " " + formJSON.timeStart;
	var postEndDatetime = formJSON.dateEnd + " " + formJSON.timeEnd;
	var postDescription = formJSON.description;

	var attendees = getMembers('attendees');
	var postAttendance = [];

	for (var i = attendees.length - 1; i >= 0; i--) {
	  postAttendance.push({"userID":attendees[i]});
	};

	var postData = JSON.stringify({
		"userID":postId,
		"title":postTitle,
		"location":postLocation,
		"datetime":postDatetime,
		"endDatetime":postEndDatetime,
		"description":postDescription,
		"attendance":postAttendance
	});
	
	$.ajax({
		type: 'POST',
		url: 'http://csse371-04.csse.rose-hulman.edu/Meeting/',
		data: postData,
		success:function(data){
			$('#newMeetingModal').modal('hide');
			window.location.reload(true);
		}
	});
}