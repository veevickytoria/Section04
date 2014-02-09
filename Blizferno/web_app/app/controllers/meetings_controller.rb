class MeetingsController < ApplicationController

	before_filter :getMeetings

	def index
		if (cookies[:userID].blank?)
			redirect_to '/login/index'
			return
		end
	end
	
	def list
	end

	def new
	end

	def getMeetings
		require 'net/http'
		url = URI.parse('http://csse371-04.csse.rose-hulman.edu/User/Meetings/' + @userID)
		req = Net::HTTP::Get.new(url.path)
		res = Net::HTTP.start(url.host, url.port) {|http|
			http.request(req)
		}
		getUserMeetings = JSON.parse(res.body)
		@meetings = Array.new
		@meetingsParsed = Array.new
		@meetingString = ''
		logger.debug "the meetings #{getUserMeetings}"
		getUserMeetings['meetings'].each do |meeting|
			url = URI.parse('http://csse371-04.csse.rose-hulman.edu/Meeting/' + meeting['id'].to_s)
			req = Net::HTTP::Get.new(url.path)
			res = Net::HTTP.start(url.host, url.port) {|http|
				http.request(req)
			}
			@meetingString = res.body
			meetingIdString = ',"meetingID":"'+meeting['id'].to_s+'"}';

			@meetingString = @meetingString[0..-2] + meetingIdString;

			@meetings.push(@meetingString)
			@meetingsParsed.push(JSON.parse(@meetingString))
		end

		
	end

	layout 'slate'
end
