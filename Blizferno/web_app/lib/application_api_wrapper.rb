require 'net/http'
require 'base_wrapper'

class ApplicationApiWrapper < BaseWrapper

	def get_user_info(user_id)
		get_response(url_from_parts('/User/', user_id.to_s))
	end

	def get_user_notifications(user_id)
		get_response(url_from_parts('/Notification/', user_id.to_s))
	end

	def get_all_users()
		get_response(url_from_parts('/User/Users/'))
	end

	def get_user_meetings(user_id)
		get_response(url_from_parts('/User/Meetings/', user_id.to_s))
	end

	def get_user_notes(user_id)
		get_response(url_from_parts('/User/Notes/', user_id.to_s))
	end

	def get_user_preferences(user_id)
		get_response(url_from_parts('/UserSettings/', user_id.to_s))
	end
end

#in /web_app/lib