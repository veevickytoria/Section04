require 'spec_helper'

describe "requests/edit" do
  before(:each) do
    @request = assign(:request, stub_model(Request,
      :user_id => 1,
      :meeting_id => 1,
      :Accepted => false,
      :Responded => false
    ))
  end

  it "renders the edit request form" do
    render

    # Run the generator again with the --webrat flag if you want to use webrat matchers
    assert_select "form[action=?][method=?]", request_path(@request), "post" do
      assert_select "input#request_user_id[name=?]", "request[user_id]"
      assert_select "input#request_meeting_id[name=?]", "request[meeting_id]"
      assert_select "input#request_Accepted[name=?]", "request[Accepted]"
      assert_select "input#request_Responded[name=?]", "request[Responded]"
    end
  end
end
