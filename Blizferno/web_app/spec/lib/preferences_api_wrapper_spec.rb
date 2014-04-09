require 'spec_helper'
require 'preferences_api_wrapper'

describe PreferencesApiWrapper do
	let(:wrapper){
		PreferencesApiWrapper.new
	}

	it 'user preferences valid' do
		userID = 645
		preferences_string = wrapper.get_user_preferences(userID)
		preferences_parsed = JSON.parse(preferences_string)
		preferences_parsed.keys.should eq ['shouldNotify', 'whenToNotify', 'type', 'tasks', 'groups', 'meetings', 'projects']

	end
end

#web_app/spec/lib/