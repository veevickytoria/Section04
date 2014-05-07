require 'task_api_wrapper'

class TasksController < ApplicationController

	before_filter :index
	before_filter :getTasks
	before_filter :getAllUsers

	def index
		if (cookies[:userID].blank?)
			redirect_to '/login/index'
			return
		end
	end

	def getTasks
		task_api_wrapper = TaskApiWrapper.new

		@listOfID = JSON.parse(task_api_wrapper.get_user_tasks(@userID))
		@tasks = Array.new
		
		taskString = ''
		taskIDs = Array.new

		@listOfID['tasks'].each do |task|
			taskID = task['id'].to_s
			taskIDs
			taskString = task_api_wrapper.get_task(taskID)
			@tasks.push(JSON.parse(taskString))
		end	
	end
	
	layout 'slate'
end
