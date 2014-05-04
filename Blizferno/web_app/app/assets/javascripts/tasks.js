function showEditTaskModal(id, users){
  var ModalFactory = abstractModalFactory();
  var modal = ModalFactory.createModal(EditTaskModal, "TaskModal", id);

  modal.showModal(users);
} 

function showDeleteTaskModal(ID){
  var ModalFactory = abstractModalFactory();
  var modal = ModalFactory.createModal(DeleteModal, "TaskModal", ID);

  modal.showModal("Task");

}

function showNewTaskModal(users, modal){
  var ModalFactory = abstractModalFactory();
  var modal = ModalFactory.createModal(NewTaskModal, modal, "");

  modal.showModal(users);
}

function showViewTaskModal(ID, users){
  var ModalFactory = abstractModalFactory();
  var modal = ModalFactory.createModal(ViewTaskModal, "TaskModal", ID);

  modal.showModal(users);
}
