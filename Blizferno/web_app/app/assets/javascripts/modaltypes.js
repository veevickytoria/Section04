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
			var span = document.createElement("span");
			var text = document.createTextNode(value);
			if(itemClass != ""){
				span.setAttribute("class", itemClass);
			}
			span.appendChild(text);
			var doc = document.getElementById(documentPosition);
			doc.appendChild(span);
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

	deleteModal.showModal = function(object){
		deleteModal.createHeader(object);
		deleteModal.createBody(object);
		deleteModal.createFooter(object);
		parentShow.call(this);
	}

	deleteModal.createHeader = function(object){
		var header = document.createElement("h1");
		header.setAttribute("class", "modal-title");
		var text = document.createTextNode("Delete " + object);
		header.appendChild(text);
		var doc = document.getElementById("header");
		doc.appendChild(header);
	}

	deleteModal.createBody = function(object){
		parentAddText.call(this,"Are you sure you want to delete this " + object + "?", "viewLabel", "body");
	}

	deleteModal.createFooter = function(object){
		// Close button
		var element = document.createElement("button");
		element.setAttribute("class", "btn btn-primary");
		element.innerHTML = "Close";
		element.setAttribute("data-dismiss", 'modal');

		var doc = document.getElementById("footer");
		doc.appendChild(element);

		// ActionButton
		var element2 = document.createElement("input");
		element2.setAttribute("type", "button");
		element2.onclick = deleteModal.executeAction(object);
		element2.setAttribute("class", "btn btn-primary");
		element2.value = "Delete";

		doc.appendChild(element2);

	}

	deleteModal.executeAction = function(object){
		$.ajax({
		    type: 'DELETE',
		    url: 'http://csse371-04.csse.rose-hulman.edu/' + object + '/' + deleteID,
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

