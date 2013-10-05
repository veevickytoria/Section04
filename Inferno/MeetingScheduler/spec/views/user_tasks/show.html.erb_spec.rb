require 'spec_helper'

describe "user_tasks/show" do
  before(:each) do
    @user_task = assign(:user_task, stub_model(UserTask,
      :user_id => 1,
      :task_id => 2
    ))
  end

  it "renders attributes in <p>" do
    render
    # Run the generator again with the --webrat flag if you want to use webrat matchers
    rendered.should match(/1/)
    rendered.should match(/2/)
  end
end
