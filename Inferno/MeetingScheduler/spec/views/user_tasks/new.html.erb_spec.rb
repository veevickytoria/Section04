require 'spec_helper'

describe "user_tasks/new" do
  before(:each) do
    assign(:user_task, stub_model(UserTask,
      :user_id => 1,
      :task_id => 1
    ).as_new_record)
  end

  it "renders new user_task form" do
    render

    # Run the generator again with the --webrat flag if you want to use webrat matchers
    assert_select "form[action=?][method=?]", user_tasks_path, "post" do
      assert_select "input#user_task_user_id[name=?]", "user_task[user_id]"
      assert_select "input#user_task_task_id[name=?]", "user_task[task_id]"
    end
  end
end
