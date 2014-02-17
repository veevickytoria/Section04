class HomePageController < ApplicationController

	before_filter :getMeetings
	before_filter :getProjects
	
	def index
		if (cookies[:userID].blank?)
			redirect_to '/login/index'
		end

		@tasks = JSON.parse('{"tasks":[{"taskID":"1","title":"Delete Task","project":"Meeting Ninja","deadline":"19-Dec-13"},
			{"taskID":"2","title":"Create a Meeting","project":"Meeting Ninja","deadline":"19-Dec-13"},
			{"taskID":"3","title":"Create a Task","project":"Meeting Ninja","deadline":"19-Dec-13"}]}')
		@meetings = JSON.parse('{"meetings":[{"meetingID":"1","title":"Baseball","location":"Room 42","datetime":"01/02/03"}, {"meetingID":"2","title":"Music","location":"Room 8675309","datetime":"04/05/06"},
			{"meetingID":"3","title":"Cars","location":"Room 409","datetime":"07/08/09"}]}')
		@groups = JSON.parse('{"groups":[{"groupID":"1","groupTitle":"NWA","groupType":"hardcore gangsta"},
			{"groupID":"2","groupTitle":"Group? More like poop!","groupType":"lolz"},
			{"groupID":"3","groupTitle":"Purgatory","groupType":"limbo"}]}')
		@projects = JSON.parse('{"projects":[{"projectID":"1","name":"Project Uno","group":"Web"},
											 {"projectID":"2","name":"Project Dos","group":"Backend"},
											 {"projectID":"3","name":"Project Tres","group":"Android"}]}')
	end
	def tabpage
	end
	def mypage
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
