require 'spec_helper'

describe "user_projects/edit" do
  before(:each) do
    @user_project = assign(:user_project, stub_model(UserProject,
      :user_id => 1,
      :project_id => 1
    ))
  end

  it "renders the edit user_project form" do
    render

    # Run the generator again with the --webrat flag if you want to use webrat matchers
    assert_select "form[action=?][method=?]", user_project_path(@user_project), "post" do
      assert_select "input#user_project_user_id[name=?]", "user_project[user_id]"
      assert_select "input#user_project_project_id[name=?]", "user_project[project_id]"
    end
  end
end
