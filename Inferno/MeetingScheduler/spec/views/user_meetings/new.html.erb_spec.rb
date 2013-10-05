require 'spec_helper'

describe "user_meetings/new" do
  before(:each) do
    assign(:user_meeting, stub_model(UserMeeting,
      :user_id => 1,
      :meeting_id => 1
    ).as_new_record)
  end

  it "renders new user_meeting form" do
    render

    # Run the generator again with the --webrat flag if you want to use webrat matchers
    assert_select "form[action=?][method=?]", user_meetings_path, "post" do
      assert_select "input#user_meeting_user_id[name=?]", "user_meeting[user_id]"
      assert_select "input#user_meeting_meeting_id[name=?]", "user_meeting[meeting_id]"
    end
  end
end
