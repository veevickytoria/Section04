require 'net/http'
require 'base_wrapper'

class ProjectApiWrapper < BaseWrapper

	def get_user_projects(user_id)
		get_response(url_from_parts('/User/Projects/', user_id.to_s))
	end

	def get_project(project_id)
		get_response(url_from_parts('/Project/', project_id.to_s))
	end
end

#in /web_app/lib