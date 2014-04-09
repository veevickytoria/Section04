var groupArray, deleteID, currentID;

function submitCreateGroupModal(){
  var invalid = validateNewValues();
  if(!invalid){
    $('#sCrGrp').addClass('disabled');
    $('#cCrGrp').addClass('disabled');
    var form = new convertFormToJSON(document.getElementById('createGroupForm'));
    // REPLACE WITH: code to validate data

    var uid = getCookie('userID');
    var postTitle = form.title;
    var members = getMembers('groupMembers');

    var postMembers = [{"userID":uid}];

    for (var i = members.length - 1; i >= 0; i--) {
      postMembers.push({"userID":members[i]});
    };

    var postData = JSON.stringify({
      "groupTitle":postTitle,
      "members":postMembers
    });

    var onSuccess = function(data){
                    $('#newGroupModal').modal('hide');
                    window.location.reload(true);
                    }
    ajaxRequest(postData, 'POST', '/Group/', true, onSuccess);
  }
}

function validateNewValues(){
  var editTaskID = currentID;
  var invalidFields = false;
  
  if(document.getElementById("newTitle") != null && document.getElementById("titleR") != null){
    if(document.getElementById("newTitle").value == ""){
      invalidFields = true;
      document.getElementById("titleR").style.display = "inline";
    }
    else{
      document.getElementById("titleR").style.display = "none";
    }
  }

  if(document.getElementById("groupMembers") != null && document.getElementById("membersR") != null){
    if(!hasSelectedValue("groupMembers")){
      invalidFields = true;
      document.getElementById("membersR").style.display = "inline";
    }
    else{
      document.getElementById("membersR").style.display = "none";
    }
  }

  return invalidFields
}

function submitEditModal(){
  var invalid = validateEditedValues();
  if(!invalid){
    var form = new convertFormToJSON(document.getElementById('updateGroupForm'));

    var postTitle = form.title;
    var members = getMembers('membersE');
    
    var postMembers = new Array();

    for (var i = members.length - 1; i >= 0; i--) {
      postMembers.push({"userID":members[i]});
    };

    //set up the data for the call
    var updateTitle = JSON.stringify({
      "groupID":currentID,
      "field":"groupTitle",
      "value":postTitle
    });

    var updateMembers = JSON.stringify({
      "groupID":currentID,
      "field":"members",
      "value":postMembers
    });

    ajaxRequest(updateTitle, 'PUT', '/Group/', false, 'null');

    var onSuccess = function(data){
                    $('#editGroupModal').modal('hide');
                    $('#viewGroupModal').modal('hide');
                    window.location.reload(true);
                    }
    ajaxRequest(updateMembers, 'PUT', '/Group/', true, onSuccess);
  }
}

function validateEditedValues(){
  var editTaskID = currentID;
  var invalidFields = false;
  
  if(document.getElementById("titleE") != null && document.getElementById("titleER") != null){
    if(document.getElementById("titleE").value == ""){
      invalidFields = true;
      document.getElementById("titleER").style.display = "inline";
    }
    else{
      document.getElementById("titleER").style.display = "none";
    }
  }

  if(document.getElementById("membersE") != null && document.getElementById("membersER") != null){
    if(!hasSelectedValue("membersE")){
      invalidFields = true;
      document.getElementById("membersER").style.display = "inline";
    }
    else{
      document.getElementById("membersER").style.display = "none";
    }
  }

  return invalidFields
}

function hideGroupModal(){
  $('#viewGroupModal').modal('hide');
}

function setGroupArray(d){
  groupArray = JSON.parse(d);
}

function showEditModalNoID(){
  showEditModal(currentID);
}

function showEditModal(id){
  var membInfo, currentMembs = new Array(), mid;
  // clear previous data in modal
  $('#editGroupModal').modal('hide');
  $('#editGroupModal').on('hidden.bs.modal', function() {
  $(this).removeData('bs.modal');
  });

  var successGroupArray = function(data){
                          setGroupArray(data);
                          }
  ajaxRequest('null', 'GET', '/Group/' + id, false, successGroupArray);

  var successGroupMembers = function(data){
                              membInfo = JSON.parse(data);
                              currentMembs.push({'mid':mid,'name':membInfo['name']});
                              }
  for (i in groupArray['members']){
    mid = groupArray['members'][i]['userID'];
    ajaxRequest('null', 'GET', '/User/' + mid, false, successGroupMembers);

  }

  document.getElementById("titleE").value = groupArray["groupTitle"];

  populateSelect([], "name", "mid", currentMembs, "membersE");
  // presetSelectObject(currentMembs, 'name', 'membersE');

  $('#editGroupModal').modal('show');
  // $('#viewGroupModal').modal('hide');
} 

function showDeleteModal(ID){
  deleteID = ID;
  $('#deleteGroupModal').modal('show');
}

function hideDeleteModal(){
    var onSuccess = function(data){
                      if(JSON.parse(data)["valid"] == "true"){
                        $('#deleteGroupModal').modal('hide');
                        window.location.reload(true);
                      }else{
                        alert('Delete Error');
                        $('#deleteGroupModal').modal('hide');
                      }
                    }
    ajaxRequest('null', 'DELETE', '/Group/' + deleteID, true, onSuccess);
}

function showGroupModalNoID(){
  showViewGroupModal(currentID);
}

function showViewGroupModal(ID){
  var membInfo;
  var membs = new Array();
  currentID = ID;
  $('#viewGroupModal').modal('hide');
  $('#viewGroupModal').on('hidden.bs.modal', function() {
    $(this).removeData('bs.modal');
  });

  var successGroupShow = function(data){
                          groupArray = JSON.parse(data);
                        }
  ajaxRequest('null', 'GET', '/Group/' + ID, false, successGroupShow);

  var successPopulateGroup = function(data){
                                membInfo = JSON.parse(data);
                                membs.push({'name':membInfo['name']});
                              }
  for (i in groupArray['members']){
    ajaxRequest('null', 'GET', '/User/' + groupArray['members'][i]['userID'], false, successPopulateGroup);
  }

  document.getElementById("titleV").innerHTML = groupArray["groupTitle"];
  populateTableRows(membs, "name", "membersV");
  
  $('#viewGroupModal').modal('show');
  $('#editGroupModal').modal('hide');
}

function validateGroup(){
  var title = document.forms["input"]["title"].value;
  var type = document.forms["input"]["type"].value;
  var desc = document.forms["input"]["desc"].value;
  var groupMembers = getMembers('grpMemSlct');
}

function getMembers(id){
  var newMembers = [];
  $( '#' + id + ' :selected' ).each( function( i, selected ) {
    newMembers[i] = $( selected ).val();
  });
  return newMembers;
}

