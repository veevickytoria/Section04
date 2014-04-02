require 'task_api_wrapper'

class TasksController < ApplicationController

	before_filter :index
	before_filter :getTasks

	def index
		if (cookies[:userID].blank?)
			redirect_to '/login/index'
			return
		end
	end

	def getTasks
		task_api_wrapper = TaskApiWrapper.new

		getUserTasks = JSON.parse(task_api_wrapper.get_user_tasks(@userID))
		@tasks = Array.new
		@tasksParsed = Array.new
		taskString = ''

		getUserTasks['tasks'].each do |task|
			taskID = task['id'].to_s

			taskString = task_api_wrapper.get_task(taskID)
			taskIdString = ',"taskID":"'+task['id'].to_s+'"}';

			taskString = taskString[0..-2] + taskIdString;

			@tasks.push(taskString)
			@tasksParsed.push(JSON.parse(taskString))
		end	
	end

	def list
	end

	def new
	end
	layout 'slate'
end
