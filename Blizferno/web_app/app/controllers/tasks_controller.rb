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
		require 'net/http'
		url = URI.parse('http://csse371-04.csse.rose-hulman.edu/User/Tasks/' + @userID)
		req = Net::HTTP::Get.new(url.path)
		res = Net::HTTP.start(url.host, url.port) {|http|
			http.request(req)
		}
		getUserTasks = JSON.parse(res.body)
		@tasks = Array.new
		@tasksParsed = Array.new
		@taskString = ''

		getUserTasks['tasks'].each do |task|
			url = URI.parse('http://csse371-04.csse.rose-hulman.edu/Task/' + task['id'].to_s)
			req = Net::HTTP::Get.new(url.path)
			res = Net::HTTP.start(url.host, url.port) {|http|
				http.request(req)
			}
			@taskString = res.body
			taskIdString = ',"taskID":"'+task['id'].to_s+'"}';

			@taskString = @taskString[0..-2] + taskIdString;

			@tasks.push(@taskString)
			@tasksParsed.push(JSON.parse(@taskString))
		end	
	end

	def list
	end

	def new
	end
	layout 'slate'
end
