function submitCreateMeetingModal(){
	$('#sCrMeet').addClass('disabled');
    $('#cCrMeet').addClass('disabled');
	var form = document.getElementById('createMeetingForm');
	var formJSON = new convertFormToJSON(form);
	var invalid = validateNewValues(formJSON);
	if (!invalid){
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

	if(document.getElementById("attendees") != null && document.getElementById("attendeesR") != null){
		if(!hasSelectedValue("attendees")){
			invalidFields = true;
			document.getElementById("attendeesR").style.display = "inline";
		}
		else{
			document.getElementById("attendeesR").style.display = "none";
		}
	}

	return invalidFields
}


//start
function showEditMeetingModal(id, users){
  var ModalFactory = abstractModalFactory();
  var modal = ModalFactory.createModal(EditMeetingModal, "MeetingModal", id);

  modal.showModal(users);
} 

function showDeleteMeetingModal(ID){
  var ModalFactory = abstractModalFactory();
  var modal = ModalFactory.createModal(DeleteModal, "MeetingModal", ID);

  modal.showModal("Meeting");

}

function showNewMeetingModal(users){
  var ModalFactory = abstractModalFactory();
  var modal = ModalFactory.createModal(NewMeetingModal, "MeetingModal", "");

  modal.showModal(users);
}

function showViewMeetingModal(ID, users){
  var ModalFactory = abstractModalFactory();
  var modal = ModalFactory.createModal(ViewMeetingModal, "MeetingModal", ID);

  modal.showModal(users);
}