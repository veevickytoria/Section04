require 'net/http'
require 'base_wrapper'

class TaskApiWrapper < BaseWrapper

	def get_user_tasks(user_id)
		get_response(url_from_parts('/User/Tasks/', user_id.to_s))
	end

	def get_task(task_id)
		get_response(url_from_parts('/Task/', task_id))
	end
end

#in /web_app/lib