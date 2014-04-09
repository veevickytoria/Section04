class ProfileController < ApplicationController
	def index
		if (cookies[:userID].blank?)
			redirect_to '/login/index'
			return
		end



		
	end
	
	def editmyprofile 
	end

	def otherProfile

		if (cookies[:otherUserID].blank?)
			redirect_to '/profile/index'
			return
		end
		
		require 'net/http'
		 urlSchedule = Net::HTTP.get(URI.parse('http://csse371-04.csse.rose-hulman.edu/User/Schedule/'+ cookies[:otherUserID]))
		 @oUserSchedule = urlSchedule

		url = URI.parse('http://csse371-04.csse.rose-hulman.edu/User/Groups/' + cookies[:otherUserID])
		req = Net::HTTP::Get.new(url.path)
		res = Net::HTTP.start(url.host, url.port) {|http|
			http.request(req)
		}
		@ogroupIDs = JSON.parse(res.body)
		@ogroups = Array.new

		groupString = ''

		@ogroupIDs['groups'].each do |group|
			url = URI.parse('http://csse371-04.csse.rose-hulman.edu/Group/' + group['groupID'].to_s)
			req = Net::HTTP::Get.new(url.path)
			res = Net::HTTP.start(url.host, url.port) {|http|
				http.request(req)
			}
			groupString = res.body

			@ogroups.push(JSON.parse(groupString))

		end

	end
	layout 'slate'

end
