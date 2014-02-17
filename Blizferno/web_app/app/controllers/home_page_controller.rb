class HomePageController < ApplicationController

	before_filter :getMeetings
	before_filter :getProjects
	before_filter :getGroups
	before_filter :getTasks
	
	def index
		if (cookies[:userID].blank?)
			redirect_to '/login/index'
		end
	end
	def tabpage
	end
	def mypage
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

	def getGroups
		# GET USER GROUPS
		require 'net/http'
		@UserID = cookies[:userID]

		# get group IDs
		url = URI.parse('http://csse371-04.csse.rose-hulman.edu/User/Groups/' + @UserID)
		req = Net::HTTP::Get.new(url.path)
		res = Net::HTTP.start(url.host, url.port) {|http|
			http.request(req)
		}

		@groupIDs = JSON.parse(res.body)
		@groups = Array.new

		groupString = ''

		@groupIDs['groups'].each do |group|
			url = URI.parse('http://csse371-04.csse.rose-hulman.edu/Group/' + group['groupID'].to_s)
			req = Net::HTTP::Get.new(url.path)
			res = Net::HTTP.start(url.host, url.port) {|http|
				http.request(req)
			}
			groupString = res.body

			@groups.push(JSON.parse(groupString))
		end
	end

	def getMeetings
		require 'net/http'
		url = URI.parse('http://csse371-04.csse.rose-hulman.edu/User/Meetings/' + @userID)
		req = Net::HTTP::Get.new(url.path)
		res = Net::HTTP.start(url.host, url.port) {|http|
			http.request(req)
		}
		getUserMeetings = JSON.parse(res.body)
		@meetings = Array.new
		@meetingsParsed = Array.new
		@meetingString = ''

		

		getUserMeetings['meetings'].each do |meeting|
			url = URI.parse('http://csse371-04.csse.rose-hulman.edu/Meeting/' + meeting['id'].to_s)
			req = Net::HTTP::Get.new(url.path)
			res = Net::HTTP.start(url.host, url.port) {|http|
				http.request(req)
			}
			@meetingString = res.body
			meetingIdString = ',"meetingID":"'+meeting['id'].to_s+'"}';

			@meetingString = @meetingString[0..-2] + meetingIdString;

			@meetings.push(@meetingString)
			@meetingsParsed.push(JSON.parse(@meetingString))
		end

		
	end

	def getProjects
  	require 'net/http'
	url = URI.parse('http://csse371-04.csse.rose-hulman.edu/User/Projects/' + @userID)
	req = Net::HTTP::Get.new(url.path)
	res = Net::HTTP.start(url.host, url.port) {|http|
		http.request(req)
	}

	getUserProjects = JSON.parse(res.body)

	@projects = Array.new
	@projectsParsed = Array.new
	projectString = ''


	getUserProjects['projects'].each do |project|
		url = URI.parse('http://csse371-04.csse.rose-hulman.edu/Project/' + project['projectID'].to_s)
		req = Net::HTTP::Get.new(url.path)
		res = Net::HTTP.start(url.host, url.port) {|http|
			http.request(req)
		}
		projectString = res.body

		projectIdString = ',"projectID":"'+project['projectID'].to_s+'"}';
		projectString = projectString[0..-2] + projectIdString;


		@projects.push(projectString)

		@projectsParsed.push(JSON.parse(projectString))
	end

  end

	layout 'slate'
end
