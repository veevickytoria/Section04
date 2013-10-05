require 'spec_helper'

describe "user_meetings/edit" do
  before(:each) do
    @user_meeting = assign(:user_meeting, stub_model(UserMeeting,
      :user_id => 1,
      :meeting_id => 1
    ))
  end

  it "renders the edit user_meeting form" do
    render

    # Run the generator again with the --webrat flag if you want to use webrat matchers
    assert_select "form[action=?][method=?]", user_meeting_path(@user_meeting), "post" do
      assert_select "input#user_meeting_user_id[name=?]", "user_meeting[user_id]"
      assert_select "input#user_meeting_meeting_id[name=?]", "user_meeting[meeting_id]"
    end
  end
end
