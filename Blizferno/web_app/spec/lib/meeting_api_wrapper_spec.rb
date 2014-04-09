require 'spec_helper'
require 'meeting_api_wrapper'

describe MeetingApiWrapper do
	let(:wrapper){
		MeetingApiWrapper.new
	}

	it 'gets meeting summaries from the database' do
		userID = 645
		#wrapper = MeetingApiWrapper.new
		meeting_summaries = wrapper.get_user_meetings(userID)
		meetings_parsed = JSON.parse(meeting_summaries)
		meetings_parsed['meetings'].each do |meeting|
			meeting.keys.should eq ['id', 'title', 'type']
		end
	end

	it 'gets meeting information' do
		meetingID = 4124.to_s
		meeting_string = wrapper.get_meeting(meetingID)
		meeting_parsed = JSON.parse(meeting_string)
		meeting_parsed.keys.should eq ['userID', 'title', 'location', 'description', 'endDatetime', 'nodeType', 'datetime', 'attendance']
	end

	it 'meeting creator valid' do
		userID = 645
		meetingID = 4124.to_s
		meeting_string = wrapper.get_meeting(meetingID)
		meeting_parsed = JSON.parse(meeting_string)
		meeting_parsed['userID'].should eq userID
	end
end

#web_app/spec/lib/