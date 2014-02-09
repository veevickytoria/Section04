var ProjectArray;
  var deleteID;
  // TODO: Remove these three
  var Meetings = JSON.parse('{"meetings":[{"meetingID":"1","title":"Weekly Update"},{"meetingID":"2","title":"Meeting with Steven"}]}');
  var Notes = JSON.parse('{"notes":[{"noteID":"1","title":"Problems"},{"noteID":"2","title":"Change Requests"}]}');
  var Members = JSON.parse('{"members":[{"userID":"1","displayName":"Lindsey"},{"userID":"2","displayName":"Corey"},{"userID":"3","displayName":"Jonathan"},{"userID":"4","displayName":"Chris"},{"userID":"5","displayName":"Grant"},{"userID":"6","displayName":"Josh"},{"userID":"7","displayName":"Paul"},{"userID":"8","displayName":"David"},{"userID":"9","displayName":"William"},{"userID":"10","displayName":"Maxwell"},{"userID":"11","displayName":"Alpha"},{"userID":"12","displayName":"Dharmin"}]}');

  function hideViewModal(){
    $('#viewProjectModal').modal('hide');
  }

  function showDeleteModal(ID){
    deleteID = ID;
    $('#deleteProjectModal').modal('show');
  }

  function hideDeleteModal(){
      // TODO: Put this back in when it works and change it to the delete
      // $.ajax({
      //         type: 'GET',
      //         url: 'http://csse371-04.csse.rose-hulman.edu/Project/' + deletelID,
      //   success:function(data){
      //     if(data.taskID != null){
      //       TaskArray = JSON.parse(data);
      //     }
      //   }
      // });
      alert("The project matching id " + deleteID + " was deleted");
      
      $('#deleteProjectModal').modal('hide');
      // TODO: Put back in
      //location.reload(true);
  }

  function showEditModal(){
    $('#editProjectModal').modal('hide');
    $('#editProjectModal').on('hidden.bs.modal', function() {
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

    document.getElementById("titleE").value = ProjectArray["projectTitle"];
    // document.getElementById("descriptionE").value = ProjectArray["description"];
    // document.getElementById("groupE").value = ProjectArray["group"];

    populateSelect(Meetings["meetings"], "title", "meetingID", ProjectArray["meetings"], "meetingsE")
    populateSelect(Notes["notes"], "title", "noteID", ProjectArray["notes"], "notesE")
    populateSelect(Members["members"], "displayName", "userID", ProjectArray["members"], "membersE")

    $('#editProjectModal').modal('show');
    $('#viewProjectModal').modal('hide');
  }

  function showViewModalNoID(){
    $('#viewProjectModal').modal('hide');
    $('#viewProjectModal').on('hidden.bs.modal', function() {
      $(this).removeData('bs.modal');
    });

    // Edit info
    ProjectArray["projectTitle"] = document.getElementById("titleE").value;
    ProjectArray["description"] = document.getElementById("descriptionE").value;
    ProjectArray["group"] = document.getElementById("groupE").value;

    ProjectArray["meetings"] = returnSelectValuesAsJSON("meetings", "title", "meetingID", "meetingsE")
    ProjectArray["notes"] = returnSelectValuesAsJSON("notes", "title", "noteID", "notesE")
    ProjectArray["members"] = returnSelectValuesAsJSON("members", "displayName", "userID", "membersE")

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
    document.getElementById("titleV").innerHTML = ProjectArray["projectTitle"];
    document.getElementById("descriptionV").innerHTML = ProjectArray["description"];
    document.getElementById("groupV").innerHTML = ProjectArray["group"];

    populateTableRows(ProjectArray["meetings"], "title", "TableMeetingsV");
    populateTableRows(ProjectArray["notes"], "title", "TableNotesV");
    populateTableRows(ProjectArray["members"], "displayName", "TableMembersV");

    $('#viewProjectModal').modal('show');
    $('#editProjectModal').modal('hide');
  }

  function showViewModal(ID){
    $('#viewProjectModal').modal('hide');
    $('#viewProjectModal').on('hidden.bs.modal', function() {
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

    if (ID == 1){
      ProjectArray = JSON.parse ('{"projectID":"1","projectTitle":"Project Uno","description":"Develop web application","group":"Web","meetings":[{"meetingID":"1","title":"Weekly Update"},{"meetingID":"2","title":"Meeting with Steven"}],"notes":[{"noteID":"1","title":"Problems"},{"noteID":"2","title":"Change Requests"}],"members":[{"userID":"1","displayName":"Lindsey"},{"userID":"2","displayName":"Corey"},{"userID":"3","displayName":"Jonathan"},{"userID":"4","displayName":"Chris"},{"userID":"5","displayName":"Grant"},{"userID":"6","displayName":"Josh"}]}');
    }else if (ID == 2){
      ProjectArray = JSON.parse('{"projectID":"2","projectTitle":"Project Dos","description":"Develop backend application","group":"Backend","meetings":[{"meetingID":"1","title":"Weekly Update"},{"meetingID":"3","title":"Meeting with Steven"}],"notes":[{"noteID":"1","title":"Problems"},{"noteID":"2","title":"Change Requests"}],"members":[{"userID":"7","displayName":"Paul"},{"userID":"8","displayName":"David"}]}');
    }else if (ID == 3){
      ProjectArray = JSON.parse('{"projectID":"3","projectTitle":"Project Tres","description":"Develop android application","group":"Android","meetings":[{"meetingID":"1","title":"Weekly Update"},{"meetingID":"2","title":"Meeting with Steven"}],"notes":[{"noteID":"1","title":"Problems"},{"noteID":"2","title":"Change Requests"}],"members":[{"userID":"9","displayName":"William"},{"userID":"10","displayName":"Maxwell"}]}');
    }else if (ID == 4){
      ProjectArray = JSON.parse('{"projectID":"4","projectTitle":"Project Cuatro","description":"Develop iOS application","group":"iOS","meetings":[{"meetingID":"1","title":"Weekly Update"},{"meetingID":"2","title":"Meeting with Steven"}],"notes":[{"noteID":"1","title":"Problems"},{"noteID":"2","title":"Change Requests"}],"members":[{"userID":"11","displayName":"Alpha"},{"userID":"12","displayName":"Dharmin"}]}');
    }

    document.getElementById("titleV").innerHTML = ProjectArray["projectTitle"];
    // document.getElementById("descriptionV").innerHTML = ProjectArray["description"];
    // document.getElementById("groupV").innerHTML = ProjectArray["group"];

    populateTableRows(ProjectArray["meetings"], "title", "TableMeetingsV");
    populateTableRows(ProjectArray["notes"], "title", "TableNotesV");
    populateTableRows(ProjectArray["members"], "displayName", "TableMembersV");
    
    $('#viewProjectModal').modal('show');
    $('#editProjectModal').modal('hide');
  }

  function populateTableRows(JSONArray, JSONDisplayColumn, tableID){
    var table = document.getElementById(tableID);

    if(table.rows.length != 0){
      for(var i = table.rows.length - 1; i > -1; i--){
        table.deleteRow(i);
      }
    }

    for (var k in JSONArray){
      var rowCount = table.rows.length;
      var row = table.insertRow(rowCount);

      var cell = row.insertCell(0);
     
      cell.innerHTML = JSONArray[k][JSONDisplayColumn];
    }
  }

  function populateSelect(JSONArray, JSONDisplayColumn, JSONValueColumn, JSONSelectValues, selectID){
    var select = document.getElementById(selectID);

    if(select.options.length != 0){
      for(var i = select.options - 1; i > -1; i--){
        select.remove(i);
      }
    }

    for (var k in JSONArray){
      var el = document.createElement("option");
      el.textContent = JSONArray[k][JSONDisplayColumn];
      el.value = JSONArray[k][JSONValueColumn];
      select.appendChild(el);
    }

    for (var i in JSONSelectValues){
      for (var j in select.options){
        if(JSONSelectValues[i][JSONValueColumn] == select.options[j].value){
          select.options[j].selected = true;
          break;
        }
      }
    }
  }

  // TODO: This will probably be usless once connected to the back end
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

function sendTheData(){
  alert("made it here");
  var userID = 5454;
  title = "Testing the connection";
  //var title = document.getElementById("projectTitle");
  alert(title);
  //var meetings = "[]";
  var meetings = '[' + JSON.stringify({meetingID:"6201"}) + ']';
  //var notes = "[]";
  var notes = '[' + JSON.stringify({noteID:"1"}) + ']';
  var members = '[' + JSON.stringify({userID:"5454"}) + ']';
  //var members = "[]";
  alert(meetings);
  alert(notes);
  alert(members);
  //var postData = JSON.stringify({projectTitle:title,meetings:meetings,notes:notes,members:members});
  var postData = '{"projectTitle":\"' +title+ '\","meetings":' +meetings+ ',"notes":' +notes+ ',"members":' +members+ '}';
  alert(postData);
  $.ajax({
    type: "POST",
    url: "http://csse371-04.csse.rose-hulman.edu/Project/",
    data: postData,
    success:function(data){
      alert("Was successful!");
    }
  });
}
