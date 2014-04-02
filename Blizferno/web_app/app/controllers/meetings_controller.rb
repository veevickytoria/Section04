require 'meeting_api_wrapper'

class MeetingsController < ApplicationController

	before_filter :index
	before_filter :getAllUsers

	def index
		if (cookies[:userID].blank?)
			redirect_to '/login/index'
			return
		end
	end

	def getMeetings

		meeting_api_wrapper = MeetingApiWrapper.new

		getUserMeetings = JSON.parse(meeting_api_wrapper.get_user_meetings(@userID))

		@meetings = Array.new
		@meetingsParsed = Array.new
		meetingString = ''
		getUserMeetings['meetings'].each do |meeting|
			meetingID = meeting['id'].to_s

			meetingString = meeting_api_wrapper.get_meeting(meetingID)

			meetingIdString = ',"meetingID":"'+meeting['id'].to_s+'"}';
			meetingString = meetingString[0..-2] + meetingIdString;

			@meetings.push(meetingString)
			
			@meetingsParsed.push(JSON.parse(meetingString))
		end
	end

	layout 'slate'
end