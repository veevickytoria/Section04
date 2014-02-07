class GroupsController < ApplicationController
	
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
		# GET USER GROUPS
		require 'net/http'
		@UserID = cookies[:userID]

		# get group IDs
		url = URI.parse('http://csse371-04.csse.rose-hulman.edu/User/Groups/' + @UserID)
		req = Net::HTTP::Get.new(url.path)
		res = Net::HTTP.start(url.host, url.port) {|http|
			http.request(req)
		}

		@groupIDs = JSON.parse(res.body)
		@groups = Array.new

		groupString = ''

		@groupIDs['groups'].each do |group|
			url = URI.parse('http://csse371-04.csse.rose-hulman.edu/Group/' + group['groupID'].to_s)
			req = Net::HTTP::Get.new(url.path)
			res = Net::HTTP.start(url.host, url.port) {|http|
				http.request(req)
			}
			groupString = res.body
			# groupString = groupString[0..-2] + ',"groupID":"' + group['groupID'].to_s + '"}';

			@groups.push(JSON.parse(groupString))

		end
	end

	layout 'slate'
end
