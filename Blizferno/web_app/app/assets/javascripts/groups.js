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

  for (i in groupArray['members']){
    mid = groupArray['members'][i]['userID'];
    var successGroupMembers = function(data){
                              membInfo = JSON.parse(data);
                              currentMembs.push({'mid':mid,'name':membInfo['name']});
                              }
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

  
  for (i in groupArray['members']){
    var successPopulateGroup = function(data){
                                membInfo = JSON.parse(data);
                                membs.push({'name':membInfo['name']});
                              }
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

<<<<<<< HEAD
function getMembers(id){
  var newMembers = [];
  $( '#' + id + ' :selected' ).each( function( i, selected ) {
    newMembers[i] = $( selected ).val();
  });
  return newMembers;
}

function populateTableRows(JSONArray, JSONDisplayColumn, tableID){
  var table = document.getElementById(tableID);
  if(table.rows.length != 0){
    for(var i = table.rows.length - 1; i > -1; i--){
      table.deleteRow(i);
    }
  }

  for (var k in JSONArray){
    // alert(k);
    var rowCount = table.rows.length;
    var row = table.insertRow(rowCount);

    var cell = row.insertCell(0);

    cell.innerHTML = JSONArray[k][JSONDisplayColumn];
  }
}

/* DOCUMENTATION FOR POPULATESELECT
  Populates the select tag specified by 'selectID'
  with text from JSONArray elements with JSONDisplayColumn data
  and value from JSONArray elements with JSONValueColumn data.
  Sets the values in JSONSelectValues to 'selected' in the newly
  populated select tag.
*/
function populateSelect(JSONArray, JSONDisplayColumn, JSONValueColumn, JSONSelectValues, selectID){
  // get select tag
  var select = document.getElementById(selectID);

  // empty select tag
  if(select.options.length != 0){
    for(var i = select.options - 1; i > -1; i--){
      select.remove(i);
    }
  }

  // populate select tag
  for (var k in JSONArray){
    var el = document.createElement("option");
    // alert(JSONArray[k][JSONDisplayColumn]);
    el.textContent = JSONArray[k][JSONDisplayColumn];
    el.value = JSONArray[k][JSONValueColumn];
    select.appendChild(el);
  }

  // set currently selected items to be selected
  for (var i in JSONSelectValues){
    for (var j in select.options){
      if(JSONSelectValues[i][JSONValueColumn] == select.options[j].value){
        select.options[j].selected = true;
        break;
      }
    }
  }
}

/* DOES NOT WORK
  A variation of populateSelect where the select tag is prefilled
  using Ruby SS code snippets. This function then takes a simple
  array and value selector as input and selects the items in the
  select box that should be seen as selected by the user.
*/
function presetSelectObject(SelectedValuesArray, JSONValueColumn, selectID){
  var selectBox = document.getElementById(selectID);
  // deselect currently selected items
  for (var i in selectBox.options){
    selectBox.options[i].selected = false;
  }

  // preselect items
  for (var j in SelectedValuesArray){
    for (var k in selectBox.options){
      if(SelectedValuesArray[j][JSONValueColumn] == selectBox.options[k].value){
        selectBox.options[k].selected = true;
        break;
      }
    }
  }
}

function returnSelectValuesAsJSON(JSONtype, JSONDisplayColumn, JSONValueColumn, selectID){
  var select = document.getElementById(selectID);
  var newJSONString = "{" + "\"" + JSONtype + "\"" + ":[";
  
  for (var j in select.options){
    if(select.options[j].selected){
      newJSONString = newJSONString + "{" + "\"" + JSONValueColumn + "\":\"" + select.options[j].value + "\",\"" + JSONDisplayColumn + "\":\"" + select.options[j].textContent + "\"},";
    }
  }

  newJSONString = newJSONString.substring(0, newJSONString.length - 1);
  newJSONString = newJSONString + "]}";
  var newJSON = JSON.parse(newJSONString);
  return newJSON[JSONtype];
}

function convertFormToJSON(form){
  var array = jQuery(form).serializeArray();
  var json = {};

  jQuery.each(array, function() {
    json[this.name] = this.value || '';
  });

  return json;
}

function hasSelectedValue(selectID){
  var select =document.getElementById(selectID);

  for (var j in select.options){
    if(select.options[j].selected){
      return true
    }
  }
  return false
}
=======
>>>>>>> 52652e0572e03bf0ee297d0486d21cbced209ad1
