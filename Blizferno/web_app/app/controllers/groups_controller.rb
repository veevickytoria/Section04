require 'group_api_wrapper'

class GroupsController < ApplicationController
	
	before_filter :index
	before_filter :getGroups
	before_filter :getAllUsers

	def index
		# CHECK IF USER LOGGED IN
		if (cookies[:userID].blank?)
			redirect_to '/login/index'
			return
		end
	end

	def getGroups
		group_api_wrapper = GroupApiWrapper.new

		@listOfID = JSON.parse(group_api_wrapper.get_user_groups(@userID))
		@groups = Array.new

		

		@listOfID['groups'].each do |group|
			groupID = group['groupID'].to_s
			groupString = "\'" + group_api_wrapper.get_group(groupID) + "\'"
			@groups.push(JSON.parse(groupString))
		end
	end

	layout 'slate'
end
