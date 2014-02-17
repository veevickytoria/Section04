  var groupArray, deleteID, currentID;

  function submitCreateModal(){
    var form = new convertFormToJSON(document.getElementById('createGroupForm'));
    // REPLACE WITH: code to validate data

    var uid = getCookie('userID');
    var postTitle = form.title;
    var members = getMembers('groupMembers');
    
    var postMembers = [{"userID":uid}];

    for (var i = members.length - 1; i >= 0; i--) {
      postMembers.push({"userID":members[i]});
    };

    //set up the data for the call
    var postData = JSON.stringify({
      "groupTitle":postTitle,
      "members":postMembers
    });

    //make the POST request to the backend
    $.ajax({
      type: 'POST',
      url: 'http://csse371-04.csse.rose-hulman.edu/Group/',
      data: postData,
      success:function(data){
        $('#newGroupModal').modal('hide');
        window.location.reload(true);
      }
    });
  }
  function submitEditModal(){
    var form = new convertFormToJSON(document.getElementById('updateGroupForm'));
    // REPLACE WITH: code to validate data

    var postTitle = form.title;
    var members = getMembers('membersE');
    
    var postMembers = new Array();

    for (var i = members.length - 1; i >= 0; i--) {
      postMembers.push({"userID":members[i]});
    };

    //set up the data for the call
    var postData = JSON.stringify({
      "groupTitle":postTitle,
      "members":postMembers
    });

    // PUT requests? phhh... who needs those? Watch this.
    $.ajax({
      type: 'DELETE',
      url: 'http://csse371-04.csse.rose-hulman.edu/Group/' + currentID,
      success:function(data){
        // do nothing
      },
      async: false
    });

    $.ajax({
      type: 'POST',
      url: 'http://csse371-04.csse.rose-hulman.edu/Group/',
      data: postData,
      success:function(data){
        $('#editGroupModal').modal('hide');
        $('#newGroupModal').modal('hide');
        window.location.reload(true);
      }
    });
  }

  function hideGroupModal(){
    $('#viewGroupModal').modal('hide');
  }

  function setGroupArray(d){
    groupArray = JSON.parse(d);
  }

  function showEditModal(){
    var membInfo, currentMembs = new Array(), mid;
    // clear previous data in modal
    $('#editGroupModal').modal('hide');
    $('#editGroupModal').on('hidden.bs.modal', function() {
    $(this).removeData('bs.modal');
    });

    $.ajax({
      type: 'GET',
      url: 'http://csse371-04.csse.rose-hulman.edu/Group/' + currentID,
      success:function(data){
        if(data.groupID != null)
          setGroupArray(data);
      }
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

    document.getElementById("titleE").value = groupArray["groupTitle"];

    // populateSelect([], "name", "mid", currentMembs, "membersE");
    presetSelectObject()

    $('#editGroupModal').modal('show');
    // $('#viewGroupModal').modal('hide');
  } 

  function showDeleteModal(ID){
    deleteID = ID;
    $('#deleteGroupModal').modal('show');
  }

  function hideDeleteModal(){
    $.ajax({
        type: 'DELETE',
        url: 'http://csse371-04.csse.rose-hulman.edu/Group/' + deleteID,
        success:function(data){
        if(JSON.parse(data)["valid"] == "true"){
          $('#deleteGroupModal').modal('hide');
          window.location.reload(true);
        }else{
          alert('Delete Error');
          $('#deleteGroupModal').modal('hide');
        }
      }
    });
  }

  function showGroupModalNoID(){
    showViewGroupModal(currentID);
    // $('#viewGroupModal').modal('hide');
    // $('#viewGroupModal').on('hidden.bs.modal', function() {
    //   $(this).removeData('bs.modal');
    // });

    // // Edit info
    // //GroupArray["groupID"] = document.getElementById("IDE").value;
    // GroupArray["groupTitle"] = document.getElementById("titleE").value;
    // GroupArray["groupType"] = document.getElementById("typeE").value;

    // GroupArray["members"] = returnSelectValuesAsJSON("members", "displayName", "userID", "membersE")

    // // TODO: Put this back in when it works and change it to update info then add a pull
    // // $.ajax({
    // //         type: '',
    // //         url: 'http://csse371-04.csse.rose-hulman.edu/Tasks/' + ID,
    // //   success:function(data){
    // //     if(data.taskID != null){
    // //       GroupArray = JSON.parse(data);
    // //     }
    // //   }
    // // });

    // // Update view info
    // //document.getElementById("IDV").innerHTML = GroupArray["groupID"];
    // document.getElementById("titleV").innerHTML = GroupArray["groupTitle"];
    // document.getElementById("typeV").innerHTML = GroupArray["groupType"];

    // populateTableRows(GroupArray["members"], "displayName", "TableMembersV");

    // $('#viewGroupModal').modal('show');
    // $('#editGroupModal').modal('hide');
  }

  function showViewGroupModal(ID){
    var membInfo;
    var membs = new Array();
    currentID = ID;
    $('#viewGroupModal').modal('hide');
    $('#viewGroupModal').on('hidden.bs.modal', function() {
      $(this).removeData('bs.modal');
    });

    $.ajax({
      type: 'GET',
      url: 'http://csse371-04.csse.rose-hulman.edu/Group/' + ID,
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

  /* DOCUMENTATION
    A variation of populateSelect where the select tag is prefilled
    using Ruby SS code snippets. This function then takes a simple
    array and value selector as input and selects the items in the
    select box that should be seen as selected by the user.
  */
  function presetSelectObject(SelectedValuesArray, JSONValueColumn){
    // deselect currently selected items
    for (var i in select.options){
      select.options[i].select = false;
    }

    // preselect items
    for (var j in SelectedValuesArray){
      for (var k in select.options){
        if(SelectedValuesArray[j][JSONValueColumn] == select.options[k].value){
          select.options[k].selected = true;
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
