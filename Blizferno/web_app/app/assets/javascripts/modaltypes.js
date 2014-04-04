// Top Abstract Modal
function NinjaModal(documentID, objectID){
	var self = this;
	return{
		showModal: function(){
			$('#' + documentID).modal('show');
		},

		addElement: function(type, value, id, name, itemClass, documentPosition){
			var element = document.createElement(input);

			if(type != ""){
				element.setAttribute("type", type);

				if(value != ""){
					element.setAttribute("value", value);
				}
				if(id != ""){
					element.setAttribute("id", id);
				}
				if(name != ""){
					element.setAttribute("name", name);
				}
				if(itemClass != ""){
					element.setAttribute("class", itemClass);
				}
				var doc = document.getElementById(documentPosition);
				doc.appendChild(element);
				doc.appendChild(document.createElement("br"));
			}
		},

		addText: function(value, itemClass, documentPosition){
			var text = document.createTextNode(value);
			if(itemClass != ""){
				text.classname = itemClass;
			}
			var doc = document.getElementById(documentPosition);
			doc.appendChild(text);
			doc.appendChild(document.createElement("br"));
		},

		close: function(){
			alert("firing")
			$('#' + documentID).modal('hide');
		}
	}
}

// Delete Abstract Modal
function DeleteModal(documentID, deleteID){
	var deleteModal = NinjaModal(documentID, deleteID);

	var parentShow = deleteModal.showModal;
	var parentAddText = deleteModal.addText;
	var parentClose = deleteModal.close;

	deleteModal.showModal = function(object){
		deleteModal.createBody(object);
		deleteModal.createFooter();
		parentShow.call(this);
	}

	deleteModal.createBody = function(object){
		parentAddText.call(this,"Are you sure you want to delete this " + object + "?", "viewLabel", "body");
	}

	deleteModal.createFooter = function(){
		// Close button
		var element = document.createElement("button");
		element.setAttribute("class", "btn btn-primary");
		element.innerHTML = "Close";
		element.setAttribute("onclick", parentClose);

		var doc = document.getElementById("footer");
		doc.appendChild(element);

		// // ActionButton
		// var element = document.createElement("input");
		// element.setAttribute("type", "button");
		// element.onclick = fuction(){ deleteModal.executeAction; };
		// element.classname = "btn btn-primary";
		// element.value = "Delete";

		// var doc = document.getElementById("footer");
		// doc.appendChild(element);

	}

	deleteModal.executeAction = function(){
		$.ajax({
		    type: 'DELETE',
		    url: 'http://csse371-04.csse.rose-hulman.edu/Group/' + deleteID,
		    success:function(data){
			    if(JSON.parse(data)["valid"] == "true"){
			    	this.close;
			        window.location.reload(true);
			    }else{
			        alert('Delete Error');
			        this.close;
			    }
		    }
		});

	}

	return deleteModal
}

