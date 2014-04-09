require 'spec_helper'
require 'project_api_wrapper'

describe ProjectApiWrapper do
	let(:wrapper){
		ProjectApiWrapper.new
	}

	it 'gets project summaries from the database' do
		userID = 645
		project_summaries = wrapper.get_user_projects(userID)
		projects_parsed = JSON.parse(project_summaries)
		projects_parsed['projects'].each do |project|
			project.keys.should eq ['id', 'title', 'type']
		end
	end

	it 'gets project information' do
		projectID = 3806.to_s
		project_string = wrapper.get_project(projectID)
		project_parsed = JSON.parse(project_string)
		project_parsed.keys.should eq ['userID', 'title', 'location', 'description', 'endDatetime', 'nodeType', 'datetime', 'attendance']
	end

	it 'project assigner valid' do
		userID = 645
		projectID = 3806.to_s
		project_string = wrapper.get_project(projectID)
		project_parsed = JSON.parse(project_string)
		project_parsed['userID'].should eq userID
	end
end

#web_app/spec/lib/