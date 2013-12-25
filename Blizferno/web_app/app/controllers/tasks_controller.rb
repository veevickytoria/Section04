class TasksController < ApplicationController
	def index
		# if (cookies[:userID].blank?)
		# 	redirect_to '/login/index'
		# 	return
		# end
		# require 'net/http'
		# @UserID = '717'
		# url = URI.parse('http://csse371-04.csse.rose-hulman.edu/User/Tasks/' + @UserID)
		# req = Net::HTTP::Get.new(url.path)
		# res = Net::HTTP.start(url.host, url.port) {|http|
		# 	http.request(req)
		# }
		#@tasks = JSON.parse(res.body)
		@tasks = JSON.parse('{"tasks":[{"taskID":"1","title":"Delete Task","project":"Meeting Ninja","deadline":"19-Dec-13"},
			{"taskID":"2","title":"Create a Meeting","project":"Meeting Ninja","deadline":"19-Dec-13"},
			{"taskID":"3","title":"Create a Task","project":"Meeting Ninja","deadline":"19-Dec-13"},
			{"taskID":"4","title":"View a Task","project":"Meeting Ninja","deadline":"19-Dec-13"}]}')

	end

	def list
	end

	def new
	end
	layout 'slate'
end
