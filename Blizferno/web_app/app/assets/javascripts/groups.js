function showEditGroupModal(id, users){
  var ModalFactory = abstractModalFactory();
  var modal = ModalFactory.createModal(EditGroupModal, "GroupModal", id);

  modal.showModal(users);
} 

function showDeleteGroupModal(ID){
  var ModalFactory = abstractModalFactory();
  var modal = ModalFactory.createModal(DeleteModal, "GroupModal", ID);

  modal.showModal("Group");

}

function showNewGroupModal(users){
  var ModalFactory = abstractModalFactory();
  var modal = ModalFactory.createModal(NewGroupModal, "GroupModal", "");

  modal.showModal(users);
}

function showViewGroupModal(ID, users){
  var ModalFactory = abstractModalFactory();
  var modal = ModalFactory.createModal(ViewGroupModal, "GroupModal", ID);

  modal.showModal(users);
}
