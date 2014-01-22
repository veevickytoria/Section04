class ProfileController < ApplicationController
	def index
		if (cookies[:userID].blank?)
			redirect_to '/login/index'
			return
		end
		@userTasks = {'tasks'=>[{'taskTitle'=>'Memo'},{'taskTitle'=>'Flyer'},{'taskTitle'=>'Drink Beer'},{'taskTitle'=>'Learn Japanese'},{'taskTitle'=>'Code Website'}]}
		@userMeetings = {'meetings'=>[{'meetingTitle'=>'Board Meeting'},{'meetingTitle'=>'Bluth Account Meeting'},{'meetingTitle'=>'Consult JCS'}]}
		@userProjects = {'projects'=>[{'projectTitle'=>'Student Schedule'},{'projectTitle'=>'371 Project'}]}





require 'net/http'
result = Net::HTTP.get(URI.parse('http://csse371-04.csse.rose-hulman.edu/User/Schedule/749'))
@userSchedule = result
	end
	def editmyprofile 
	end
	layout 'slate'

end
