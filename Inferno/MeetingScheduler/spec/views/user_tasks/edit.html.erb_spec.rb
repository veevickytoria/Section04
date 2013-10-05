require 'spec_helper'

describe "user_tasks/edit" do
  before(:each) do
    @user_task = assign(:user_task, stub_model(UserTask,
      :user_id => 1,
      :task_id => 1
    ))
  end

  it "renders the edit user_task form" do
    render

    # Run the generator again with the --webrat flag if you want to use webrat matchers
    assert_select "form[action=?][method=?]", user_task_path(@user_task), "post" do
      assert_select "input#user_task_user_id[name=?]", "user_task[user_id]"
      assert_select "input#user_task_task_id[name=?]", "user_task[task_id]"
    end
  end
end
