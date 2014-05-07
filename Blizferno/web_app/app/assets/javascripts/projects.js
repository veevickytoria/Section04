function showEditProjectModal(id, users, meetings){
  var ModalFactory = abstractModalFactory();
  var modal = ModalFactory.createModal(EditGroupModal, "ProjectModal", id);

  modal.showModal(users);
} 

function showDeleteProjectModal(ID){
  var ModalFactory = abstractModalFactory();
  var modal = ModalFactory.createModal(DeleteModal, "ProjectModal", ID);

  modal.showModal("Group");

}

function showNewProjectModal(users, meetings){
  var ModalFactory = abstractModalFactory();
  var modal = ModalFactory.createModal(NewProjectModal, "ProjectModal", "");

  modal.showModal(users);
}

function showViewProjectModal(ID, users, meetings){
  var ModalFactory = abstractModalFactory();
  var modal = ModalFactory.createModal(ViewProjectModal, "ProjectModal", ID);

  modal.showModal(users);
}







// function validateNewValues(JSONForm){
//   var invalidFields = false;

//   for (key in JSONForm){
//     if(document.getElementById(key+"R") != null){
//       if(JSONForm[key] == ""){
//         invalidFields = true;
//         document.getElementById(key+"R").style.display = "inline";
//       }
//       else{
//         document.getElementById(key+"R").style.display = "none";
//       }
//     }
//   }

//   if(document.getElementById("meetings") != null && document.getElementById("meetingsR") != null){
//     if(!hasSelectedValue("meetings")){
//       invalidFields = true;
//       document.getElementById("meetingsR").style.display = "inline";
//     }
//     else{
//       document.getElementById("meetingsR").style.display = "none";
//     }
//   }

//   if(document.getElementById("members") != null && document.getElementById("membersR") != null){
//     if(!hasSelectedValue("members")){
//       invalidFields = true;
//       document.getElementById("membersR").style.display = "inline";
//     }
//     else{
//       document.getElementById("membersR").style.display = "none";
//     }
//   }

//   return invalidFields
// }

// function submitCreateProject(){
//   $('#sCrProj').addClass('disabled');
//   $('#cCrProj').addClass('disabled');
//   var form = document.getElementById('createProject');

//   var formJSON = new convertFormToJSON(form);
//   var invalid = validateNewValues(formJSON);
//   if (!invalid){
//     var projectTitle = formJSON.projectTitle;
    
//     var meetings = getElements('meetings');
//     var notes = getElements('notes');
//     var members = getElements('members');

//     var id = getCookie('userID');

//     var postMeetings = [];
//     var postNotes = [];
//     var postMembers = [];

//     for (var i = meetings.length - 1; i>=0; i--){
//       postMeetings.push({"meetingID":meetings[i]});
//     };

//     for (var i = notes.length - 1; i>=0; i--){
//       postNotes.push({"noteID":notes[i]});
//     };

//     postMembers.push({"userID":id});
//     for (var i = members.length - 1; i>= 0; i--) {
//       postMembers.push({"userID":members[i]});
//     };

//     var postData = JSON.stringify({
//         "projectTitle":projectTitle,
//         "meetings":postMeetings,
//         "notes":postNotes,
//         "members":postMembers
//       });

//     $.ajax({
//       type: 'POST',
//       url: 'http://csse371-04.csse.rose-hulman.edu/Project/',
//       data: postData,
//       success:function(data){
//         $('#newProjectModal').modal('hide');
//         window.location.reload(true);
//       }
//     });
//   }
// }

// function getElements(id){
//   var elements = [];
//   $( '#' + id + ' :selected' ).each( function( i, selected ) {
//     elements[i] = $( selected ).val();
//   });
//   return elements;
// }

// function hasSelectedValue(selectID){
//   var select =document.getElementById(selectID);

//   for (var j in select.options){
//     if(select.options[j].selected){
//       return true
//     }
//   }

//     return false
// }