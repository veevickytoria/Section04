/*
function hideViewModal(){
$('#viewMeetingModel').modal('hide');
}
function showViewModal2(){
$('#viewMeetingModal').modal('show');
$('#editMeetingModal').modal('hide');
}
function showViewModal(ID){
$('#viewMeetingModal').modal('show');
}
*/

var currentID;
var deleteID;
var meetingsJSON = <%= raw @meetings %>;
meetingsJSON = JSON.parse("["+meetingsJSON.toString()+"]");

function hideMeetingModal(){
	$('#viewMeetingModal').modal('hide');
}

function showEditModal(){
	$('#editMeetingModal').modal('hide');
	$('#editMeetingModal').on('hidden.bs.modal', function() {
		$(this).removeData('bs.modal');
	});

	// TODO: Put this back in when it works
	// $.ajax({
	//         type: 'GET',
	//         url: 'http://csse371-04.csse.rose-hulman.edu/Tasks/' + ID,
	//   success:function(data){
	//     if(data.taskID != null){
	//       TaskArray = JSON.parse(data);
	//     }
	//   }
	// });

	document.getElementById("titleE").value = meetingsJSON[currentID]["title"];
	document.getElementById("descriptionE").value = meetingsJSON[currentID]["description"];
	document.getElementById("locationE").value = meetingsJSON[currentID]["location"];
	document.getElementById("datetimeE").value = meetingsJSON[currentID]["datetime"];
	// document.getElementById("attendanceE").value = TaskArray["attendance"];

	$('#editMeetingModal').modal('show');
	$('#viewMeetingModal').modal('hide');
}

function updateEditedValues(){

}

function showDeleteModal(ID){
	deleteID = ID;
	$('#deleteMeetingModal').modal('show');
}

function hideDeleteModal(){
	// TODO: Put this back in when it works and change it to the delete
	$.ajax({
		type: 'DELETE',
		url: 'http://csse371-04.csse.rose-hulman.edu/Meeting/' + meetingsJSON[deleteID]['meetingID'],
		success:function(data){
			if(data.deleted == true){
				delete meetingsJSON[deleteID];
			}
		}
	});
	//alert("The task matching id " + deleteID + " was deleted");

	$('#deleteMeetingModal').modal('hide');
	// TODO: Put back in
	location.reload(true);
}

function showMeetingModalNoID(){
	$('#viewMeetingModal').modal('hide');
	$('#viewMeetingModal').on('hidden.bs.modal', function() {
		$(this).removeData('bs.modal');
	});

	// Edit info
	meetingsJSON[currentID]["title"] = document.getElementById("titleE").value;
	meetingsJSON[currentID]["description"] = document.getElementById("descriptionE").value;
	meetingsJSON[currentID]["location"] = document.getElementById("locationE").value;
	meetingsJSON[currentID]["datetime"] = document.getElementById("datetimeE").value;

	// TODO: Put this back in when it works and change it to update info then add a pull
	// $.ajax({
	//         type: '',
	//         url: 'http://csse371-04.csse.rose-hulman.edu/Tasks/' + ID,
	//   success:function(data){
	//     if(data.taskID != null){
	//       TaskArray = JSON.parse(data);
	//     }
	//   }
	// });

	// Update view info
	document.getElementById("titleV").innerHTML = meetingsJSON[currentID]["title"];
	document.getElementyById("descriptionV").innerHTML = meetingsJSON[currentID]["description"];
	document.getElementById("locationV").innerHTML = meetingsJSON[currentID]["location"];
	document.getElementById("datetimeV").innerHTML = meetingsJSON[currentID]["datetime"];
	// document.getElementById("attendanceV").innerHTML = TaskArray["attendance"];

	$('#viewMeetingModal').modal('show');
	$('#editMeetingModal').modal('hide');
}

function showViewMeetingModal(ID){
	$('#viewMeetingModal').modal('hide');
	$('#viewMeetingModal').on('hidden.bs.modal', function() {
		$(this).removeData('bs.modal');
	});

	currentID = ID;

	document.getElementById("titleV").innerHTML = meetingsJSON[currentID]["title"];
	document.getElementById("descriptionV").innerHTML = meetingsJSON[currentID]["description"];
	document.getElementById("locationV").innerHTML = meetingsJSON[currentID]["location"];
	document.getElementById("datetimeV").innerHTML = meetingsJSON[currentID]["datetime"];

	$('#viewMeetingModal').modal('show');
	$('#editMeetingModal').modal('hide');
}

function validateForm(){
	//QUESTION: Is this function used/planned for something?
	var title = document.forms["input"]["title"].value;
	// var attendees = document.forms["input"]["attendees"].value;
	var datetime = document.forms["input"]["datetime"].value;
	var location = document.forms["input"]["location"].value;
	alert("The following fields were added to the db: " + title + " " + datetime + " " + location);

	return true;
}