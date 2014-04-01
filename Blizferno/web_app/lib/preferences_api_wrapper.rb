require 'net/http'
require 'base_wrapper'

class PreferencesApiWrapper < BaseWrapper

	def get_user_preferences(user_id)
		get_response(url_from_parts('/UserSettings/', user_id.to_s))
	end
end

#in /web_app/lib