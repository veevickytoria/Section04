function showEditModal(id, users){
  var ModalFactory = abstractModalFactory();
  var modal = ModalFactory.createModal(EditGroupModal, "GroupModal", id);

  modal.showModal(users);
} 

function showDeleteModal(ID){
  var ModalFactory = abstractModalFactory();
  var modal = ModalFactory.createModal(DeleteModal, "GroupModal", ID);

  modal.showModal("Group");

}

function showNewModal(users){
  var ModalFactory = abstractModalFactory();
  var modal = ModalFactory.createModal(NewGroupModal, "GroupModal", "");

  modal.showModal(users);
}

function showViewGroupModal(ID){
  var ModalFactory = abstractModalFactory();
  var modal = ModalFactory.createModal(ViewGroupModal, "GroupModal");

  modal.showModal();
}
