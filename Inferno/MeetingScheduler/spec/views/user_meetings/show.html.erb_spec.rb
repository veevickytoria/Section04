require 'spec_helper'

describe "user_meetings/show" do
  before(:each) do
    @user_meeting = assign(:user_meeting, stub_model(UserMeeting,
      :user_id => 1,
      :meeting_id => 2
    ))
  end

  it "renders attributes in <p>" do
    render
    # Run the generator again with the --webrat flag if you want to use webrat matchers
    rendered.should match(/1/)
    rendered.should match(/2/)
  end
end
