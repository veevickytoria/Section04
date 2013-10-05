require 'spec_helper'

describe "requests/new" do
  before(:each) do
    assign(:request, stub_model(Request,
      :user_id => 1,
      :meeting_id => 1,
      :Accepted => false,
      :Responded => false
    ).as_new_record)
  end

  it "renders new request form" do
    render

    # Run the generator again with the --webrat flag if you want to use webrat matchers
    assert_select "form[action=?][method=?]", requests_path, "post" do
      assert_select "input#request_user_id[name=?]", "request[user_id]"
      assert_select "input#request_meeting_id[name=?]", "request[meeting_id]"
      assert_select "input#request_Accepted[name=?]", "request[Accepted]"
      assert_select "input#request_Responded[name=?]", "request[Responded]"
    end
  end
end
