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
		var elementClose = document.createElement("button");
		elementClose.setAttribute("class", "btn btn-primary");
		elementClose.innerHTML = "Close";
		elementClose.setAttribute("data-dismiss", 'modal');

		
		

		// // ActionButton
		var elementDelete = document.createElement("input");
		elementDelete.setAttribute("type", "button");
		elementDelete.onclick = deleteModal.executeAction;
		elementDelete.setAttribute("class", "btn btn-primary");
		elementDelete.value = "Delete";

		var doc = document.getElementById("footer");
		doc.appendChild(elementClose);
		doc.appendChild(elementDelete);

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

