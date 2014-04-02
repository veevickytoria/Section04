require 'preferences_api_wrapper'
require 'meeting_api_wrapper'
require 'task_api_wrapper'
require 'group_api_wrapper'
require 'project_api_wrapper'

class HomePageController < ApplicationController

	before_filter :index
	before_filter :getSettings
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

  def getSettings
  	preferences_api_wrapper = PreferencesApiWrapper.new
    settings = JSON.parse(preferences_api_wrapper.get_user_preferences(@userID))

    @taskVal = settings['tasks']
    @groupVal = settings['groups']
    @projectVal = settings['projects']
    @meetingVal = settings['meetings']
  end

	def getTasks
		task_api_wrapper = TaskApiWrapper.new
		getUserTasks = JSON.parse(task_api_wrapper.get_user_tasks(@userID))
		@tasks = Array.new
		@tasksParsed = Array.new
		@taskString = ''

		getUserTasks['tasks'].each do |task|
			taskID = task['id'].to_s
			@taskString = task_api_wrapper.get_task(taskID)
			taskIdString = ',"taskID":"'+ taskID +'"}';

			@taskString = @taskString[0..-2] + taskIdString;

			@tasks.push(@taskString)
			@tasksParsed.push(JSON.parse(@taskString))
		end	
	end

	def getGroups
		group_api_wrapper = GroupApiWrapper.new
		@groupIDs = JSON.parse(group_api_wrapper.get_user_groups(@userID))
		@groups = Array.new

		groupString = ''

		@groupIDs['groups'].each do |group|
			groupID = group['groupID'].to_s
			groupString = group_api_wrapper.get_group(groupID)

			@groups.push(JSON.parse(groupString))
		end
	end

	def getMeetings
		meeting_api_wrapper = MeetingApiWrapper.new
		getUserMeetings = JSON.parse(meeting_api_wrapper.get_user_meetings(@userID))
		@meetings = Array.new
		@meetingsParsed = Array.new
		@meetingString = ''

		

		getUserMeetings['meetings'].each do |meeting|
			meetingID = meeting['id'].to_s
			@meetingString = meeting_api_wrapper.get_meeting(meetingID)
			meetingIdString = ',"meetingID":"'+ meetingID +'"}';

			@meetingString = @meetingString[0..-2] + meetingIdString;

			@meetings.push(@meetingString)
			@meetingsParsed.push(JSON.parse(@meetingString))
		end

		
	end

	def getProjects
  	project_api_wrapper = ProjectApiWrapper.new
	getUserProjects = JSON.parse(project_api_wrapper.get_user_projects(@userID))

	@projects = Array.new
	@projectsParsed = Array.new
	projectString = ''


	getUserProjects['projects'].each do |project|
		projectID = project['projectID'].to_s
		projectString = project_api_wrapper.get_project(projectID)

		projectIdString = ',"projectID":"'+ projectID +'"}';
		projectString = projectString[0..-2] + projectIdString;


		@projects.push(projectString)

		@projectsParsed.push(JSON.parse(projectString))
	end

  end

	layout 'slate'
end
