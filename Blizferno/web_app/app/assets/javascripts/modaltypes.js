// Top Abstract Modal
function NinjaModal(documentID, objectID){
	var self = this;
	return{
		showModal: function(){
			$('#' + documentID).modal('show');
		},

		addElement: function(type, value, id, name, itemClass, documentPosition){
			var element = document.createElement("input");

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
			}
		},

		addText: function(value, id, itemClass, documentPosition){
			var span = document.createElement("span");
			var text = document.createTextNode(value);
			if(itemClass != ""){
				span.setAttribute("class", itemClass);
			}
			if(id != ""){
					span.setAttribute("id", id);
			}
			span.appendChild(text);
			var doc = document.getElementById(documentPosition);
			doc.appendChild(span);

			var el = document.createElement("br");
			doc.appendChild(el);
		},

		addBreak: function(documentPosition){
			var el = document.createElement("br");
			var doc = document.getElementById(documentPosition);
			doc.appendChild(el);
		},

		close: function(){
			var div = document.getElementById('header');
			while(div.firstChild){
    			div.removeChild(div.firstChild);
			}
			var div2 = document.getElementById('body');
			while(div2.firstChild){
    			div2.removeChild(div2.firstChild);
			}
			var div3 = document.getElementById('footer');
			while(div3.firstChild){
    			div3.removeChild(div3.firstChild);
			}

			$('#' + documentID).on('hidden.bs.modal', function() {
				$(this).removeData('bs.modal');
			});

		},

		/* From this point on, the methods are methods that do not pertain 
			to the modal but are used in every modal for element population
		*/ 

		returnSelectValuesAsJSON: function(JSONtype, JSONDisplayColumn, JSONValueColumn, selectID){
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
		},

		convertFormToJSON: function(form){
			var array = jQuery(form).serializeArray();
			var json = {};

			jQuery.each(array, function() {
				json[this.name] = this.value || '';
			});

			return json;
		},

		hasSelectedValue: function(selectID){
			var select = selectID;

			for (var j in select.options){
				if(select.options[j].selected){
					return true
				}
			}
			return false
		},

		/* DOCUMENTATION FOR POPULATESELECT
			Populates the select tag specified by 'selectID'
			with text from JSONArray elements with JSONDisplayColumn data
			and value from JSONArray elements with JSONValueColumn data.
			Sets the values in JSONSelectValues to 'selected' in the newly
			populated select tag.
		*/
		
		populateSelect: function(JSONArray, JSONDisplayColumn, JSONValueColumn, JSONSelectValues, selectID){
			// get select tag
			var select = selectID;
			// empty select tag
			if(select.options.length != 0){
				for(var i = select.options - 1; i > -1; i--){
					select.remove(i);
				}
			}

			// populate select tag
			for (var k in JSONArray){
				if(JSONArray[k][JSONDisplayColumn] != "" && JSONArray[k][JSONDisplayColumn] != null){
					var el = document.createElement("option");
					el.textContent = JSONArray[k][JSONDisplayColumn];
					el.value = JSONArray[k][JSONValueColumn];
					select.appendChild(el);
				}
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

			return select;
		},

		populateTableRows: function(JSONArray, JSONDisplayColumn, tableID){
			var table = tableID
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

			return table
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
		parentAddText.call(this,"Are you sure you want to delete this " + object + "?","", "viewLabel", "body");
	}

	deleteModal.createFooter = function(object){
		var self = this;
		// Close button
		var elementClose = document.createElement("button");
		elementClose.setAttribute("class", "btn btn-primary");
		elementClose.innerHTML = "Close";
		elementClose.onclick = function(){self.close();};

		// ActionButton
		var elementDelete = document.createElement("input");
		elementDelete.setAttribute("type", "button");
		elementDelete.onclick = function(){self.executeAction(object);};
		elementDelete.setAttribute("class", "btn btn-primary");
		elementDelete.value = "Delete";

		var doc = document.getElementById("footer");
		doc.appendChild(elementClose);
		doc.appendChild(elementDelete);

	}

	deleteModal.close = function(){
		parentClose.call(this);
		$('#' + documentID).modal('hide');
	}

	deleteModal.executeAction = function(object){
		$.ajax({
		    type: 'DELETE',
		    url: 'http://csse371-04.csse.rose-hulman.edu/' + object + '/' + deleteID,
		    success:function(data){
			    if(JSON.parse(data)["valid"] == "true"){
			    	parentClose.call(this);
			        window.location.reload(true);
			    }else{
			        alert('Delete Error');
			        parentClose.call(this);
			        this.close;
			    }
		    }
		});

	}

	return deleteModal
}
