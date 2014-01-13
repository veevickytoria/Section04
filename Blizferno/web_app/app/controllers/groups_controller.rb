class GroupsController < ApplicationController
	def index
		if (cookies[:userID].blank?)
			redirect_to '/login/index'
			return
		end
		require 'net/http'
		@UserID = '717'
		url = URI.parse('http://csse371-04.csse.rose-hulman.edu/User/Groups/' + @UserID)
		req = Net::HTTP::Get.new(url.path)
		res = Net::HTTP.start(url.host, url.port) {|http|
			http.request(req)
		}


		#@tasks = JSON.parse(res.body)
		@groups = JSON.parse('{"groups":[{"groupID":"1","groupTitle":"NWA","groupType":"hardcore gangsta"},
			{"groupID":"2","groupTitle":"Group? More like poop!","groupType":"lolz"},
			{"groupID":"3","groupTitle":"KKK","groupType":"racist"},
			{"groupID":"4","groupTitle":"Murdah Squad","groupType":"serial killer"}]}')

	end

	def new
	end

	layout 'slate'
end
