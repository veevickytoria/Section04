function abstractModalFactory() {
	return {
      createModal: function (type, documentID, objectID) {
          var Modal = type;
          return (Modal ? new Modal(documentID, objectID) : null);
    	}
	}
}