require 'group_api_wrapper'

class GroupsController < ApplicationController
	
	before_filter :getGroups
	before_filter :getAllUsers

	def getGroups
		group_api_wrapper = GroupApiWrapper.new

		@listOfID = JSON.parse(group_api_wrapper.get_user_groups(@userID))
		@groups = Array.new

		groupString = ''

		@listOfID['groups'].each do |group|
			groupID = group['groupID'].to_s
			groupString = group_api_wrapper.get_group(groupID) 
			@groups.push(JSON.parse(groupString))
		end
	end

	layout 'slate'
end