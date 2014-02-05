class ProfileController < ApplicationController
	def index
		if (cookies[:userID].blank?)
			redirect_to '/login/index'
			return
		end
		# @userProjects = {'projects'=>[{'projectTitle'=>'Student Schedule'},{'projectTitle'=>'371 Project'}]}
		# @userGroups = {'groups'=>[{'groupTitle'=>'IAIT'},{'groupTitle'=>'ChandanSrPrj'},{'groupTitle'=>'FIJISucks'}]}



require 'net/http'
result = Net::HTTP.get(URI.parse('http://csse371-04.csse.rose-hulman.edu/User/Schedule/'+cookies[:userID]+''))
@userSchedule = result

result = Net::HTTP.get(URI.parse('http://csse371-04.csse.rose-hulman.edu/User/Groups/'+cookies[:userID]+''))
@userGroups = result

esult = Net::HTTP.get(URI.parse('http://csse371-04.csse.rose-hulman.edu/User/Projects/'+cookies[:userID]+''))
@userProjects = result

	end
	def editmyprofile 
	end
	layout 'slate'

end
