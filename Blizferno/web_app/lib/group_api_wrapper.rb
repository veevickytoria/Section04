require 'net/http'
require 'base_wrapper'

class GroupApiWrapper < BaseWrapper

	def get_user_groups(user_id)
		get_response(url_from_parts('/User/Groups/', user_id.to_s))
	end

	def get_group(group_id)
		get_response(url_from_parts('/Group/', group_id))
	end
end

#in /web_app/lib