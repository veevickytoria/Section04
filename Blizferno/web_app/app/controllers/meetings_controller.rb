class MeetingsController < ApplicationController
	def index
		if (cookies[:userID].blank?)
			redirect_to '/login/index'
			return
		end
		require 'net/http'
		@UserID = '717'
		url = URI.parse('http://csse371-04.csse.rose-hulman.edu/User/Meetings/' + @UserID)
		req = Net::HTTP::Get.new(url.path)
		res = Net::HTTP.start(url.host, url.port) {|http|
			http.request(req)
		}
		@meetings = JSON.parse('{"meetings":[{"meetingID":"1","title":"Baseball","location":"Room 42","datetime":"01/02/03"}, {"meetingID":"2","title":"Music","location":"Room 8675309","datetime":"04/05/06"},
			{"meetingID":"3","title":"Cars","location":"Room 409","datetime":"07/08/09"},
			{"meetingID":"4","title":"Drink Beer!","location":"Frat Houses","datetime":"10/11/12"},
			{"meetingID":"5","title":"Drink More Beer!","location":"Different Frat Houses","datetime":"12/13/14"}]}')
	end
	
	def list
	end

	def new
	end
	layout 'slate'
end
