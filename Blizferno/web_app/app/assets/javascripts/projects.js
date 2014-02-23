function submitCreateProject(){
    $('#sCrProj').addClass('disabled');
    $('#cCrProj').addClass('disabled');
    var form = document.getElementById('createProject');

    var formJSON = new convertFormToJSON(form);

    var projectTitle = formJSON.projectTitle;
    
    var meetings = getElements('meetings');
    var notes = getElements('notes');
    var members = getElements('members');

    var id = getCookie('userID');

    var postMeetings = [];
    var postNotes = [];
    var postMembers = [];

    for (var i = meetings.length - 1; i>=0; i--){
      postMeetings.push({"meetingID":meetings[i]});
    };

    for (var i = notes.length - 1; i>=0; i--){
      postNotes.push({"noteID":notes[i]});
    };

    postMembers.push({"userID":id});
    for (var i = members.length - 1; i>= 0; i--) {
      postMembers.push({"userID":members[i]});
    };

    var postData = JSON.stringify({
        "projectTitle":projectTitle,
        "meetings":postMeetings,
        "notes":postNotes,
        "members":postMembers
      });

    $.ajax({
      type: 'POST',
      url: 'http://csse371-04.csse.rose-hulman.edu/Project/',
      data: postData,
      success:function(data){
        $('#newProjectModal').modal('hide');
        window.location.reload(true);
      }
    });
}

function getElements(id){
    var elements = [];
    $( '#' + id + ' :selected' ).each( function( i, selected ) {
      elements[i] = $( selected ).val();
    });
    return elements;
  }