require 'project_api_wrapper'

class ProjectsController < ApplicationController

	before_filter :index
	before_filter :getProjects

  def index
  	if (cookies[:userID].blank?)
			redirect_to '/login/index'
			return
	end
  end

  def list
  end

  def new
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

		projectIdString = ',"projectID":"' + projectID + '"}';
		projectString = projectString[0..-2] + projectIdString;


		@projects.push(projectString)

		@projectsParsed.push(JSON.parse(projectString))
	end

  end
  layout 'slate'
end
