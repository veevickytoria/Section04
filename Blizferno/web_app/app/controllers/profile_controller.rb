class ProfileController < ApplicationController
	def index
		@userTasks = {'tasks'=>[{'taskTitle'=>'Memo'},{'taskTitle'=>'Flyer'},{'taskTitle'=>'Drink Beer'},{'taskTitle'=>'Learn Japanese'},{'taskTitle'=>'Code Website'}]}
		@userMeetings = {'meetings'=>[{'meetingTitle'=>'Board Meeting'},{'meetingTitle'=>'Bluth Account Meeting'},{'meetingTitle'=>'Consult JCS'}]}
		@userProjects = {'projects'=>[{'projectTitle'=>'Student Schedule'},{'projectTitle'=>'371 Project'}]}
	end
	def editmyprofile 
	end
	layout 'slate'

end