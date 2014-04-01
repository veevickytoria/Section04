require 'net/http'
require 'base_wrapper'

class MeetingApiWrapper < BaseWrapper

	def get_user_meetings(user_id)
		get_response(url_from_parts('/User/Meetings/', user_id.to_s))
	end

	def get_meeting(meeting_id)
		get_response(url_from_parts('/Meeting/', meeting_id))
	end

	def get_user_information(path, user_id)
		get_response(url_from_parts(path, user_id.to_s))
	end
end

#in /web_app/lib