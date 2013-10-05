require 'spec_helper'

describe "user_tasks/index" do
  before(:each) do
    assign(:user_tasks, [
      stub_model(UserTask,
        :user_id => 1,
        :task_id => 2
      ),
      stub_model(UserTask,
        :user_id => 1,
        :task_id => 2
      )
    ])
  end

  it "renders a list of user_tasks" do
    render
    # Run the generator again with the --webrat flag if you want to use webrat matchers
    assert_select "tr>td", :text => 1.to_s, :count => 2
    assert_select "tr>td", :text => 2.to_s, :count => 2
  end
end
