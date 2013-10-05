require 'spec_helper'

describe "requests/show" do
  before(:each) do
    @request = assign(:request, stub_model(Request,
      :user_id => 1,
      :meeting_id => 2,
      :Accepted => false,
      :Responded => false
    ))
  end

  it "renders attributes in <p>" do
    render
    # Run the generator again with the --webrat flag if you want to use webrat matchers
    rendered.should match(/1/)
    rendered.should match(/2/)
    rendered.should match(/false/)
    rendered.should match(/false/)
  end
end
