class ProfileController < ApplicationController
	def index
		if (cookies[:userID].blank?)
			redirect_to '/login/index'
			return
		end



		require 'net/http'
		 urlSchedule = Net::HTTP.get(URI.parse('http://csse371-04.csse.rose-hulman.edu/User/Schedule/'+cookies[:userID]))
		 @userSchedule = urlSchedule

		url = URI.parse('http://csse371-04.csse.rose-hulman.edu/User/Groups/' + cookies[:userID])
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

			@groups.push(JSON.parse(groupString))

		end


		# TODO fix backend then uncomment code
		# urlProjects = URI.parse('http://csse371-04.csse.rose-hulman.edu/User/Project/' + cookies[:userID])
		# req = Net::HTTP::Get.new(urlProjects.path)
		# res = Net::HTTP.start(urlProjects.host, urlProjects.port) {|http|
		# 	http.request(req)
		# }
		# @projectIDs = JSON.parse(res.body)
		# @project = Array.new

		# @projectString = ''

		# @projectIDs['project'].each do |project|
		# 	url = URI.parse('http://csse371-04.csse.rose-hulman.edu/Project/' + group['groupID'].to_s)
		# 	req = Net::HTTP::Get.new(url.path)
		# 	res = Net::HTTP.start(url.host, url.port) {|http|
		# 		http.request(req)
		# 	}
		# 	projectString = res.body

		# 	@project.push(JSON.parse(projectString))

		# end

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
