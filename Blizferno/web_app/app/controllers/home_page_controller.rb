class HomePageController < ApplicationController
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
	layout 'slate'
end
