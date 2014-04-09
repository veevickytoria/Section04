require 'spec_helper'
require 'task_api_wrapper'

describe TaskApiWrapper do
	let(:wrapper){
		TaskApiWrapper.new
	}

	it 'gets task summaries from the database' do
		userID = 645
		task_summaries = wrapper.get_user_tasks(userID)
		tasks_parsed = JSON.parse(task_summaries)
		tasks_parsed['tasks'].each do |task|
			task.keys.should eq ['id', 'title', 'type']
		end
	end

	it 'gets task information' do
		userID = 645
		taskID = 4535.to_s
		task_string = wrapper.get_task(taskID)
		task_parsed = JSON.parse(task_string)
		task_parsed.keys.should eq ['completionCriteria', 'title', 'description', 'dateCreated', 'nodeType', 'deadline', 'isCompleted', 'assignedTo', 'assignedFrom', 'createdBy', 'taskID']
	end

	it 'task assigner valid' do
		userID = 645
		taskID = 4535.to_s
		task_string = wrapper.get_task(taskID)
		task_parsed = JSON.parse(task_string)
		task_parsed['assignedFrom'].should eq userID
	end
end

#web_app/spec/lib/