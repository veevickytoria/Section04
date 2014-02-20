class MeetingsController < ApplicationController

	before_filter :index
	#The before_filter will call the functions listed before the page is served. In this case we are using it to get all meetings associated with our user before we navigate to the meetings page. Currently the way we are going about getting the meetings is synchronous (single-thread) so the controller has to wait to load all of the meetings from the backend before it can serve the page. This is why it takes so long to load the page. Ideally we will change this to an asynchronous (multi-thread) method so that the page can render and display individual meetings as they get loaded.
 	before_filter :getMeetings
	before_filter :getAllUsers

	#The controller will call this function when the browser is directed to the /meetings/index page which is where we direct them when they click on the meetings tab. Currently this function checks to see if we have a cookie for them. If we don't we redirect the browser to the login page.
	def index
		if (cookies[:userID].blank?)
			redirect_to '/login/index'
			return
		end
	end

	#This function gets all meetings attached to a user and stores them as a JSON object that we can then access in our html and js files. 
	def getMeetings

		#This section of the function is making a call to the backend (a GET request) the so called "Rails way". First make sure there is access to the appropriate library. Then set the URL, send the request, and recieve the response.
		require 'net/http'
		url = URI.parse('http://csse371-04.csse.rose-hulman.edu/User/Meetings/' + @userID)
		req = Net::HTTP::Get.new(url.path)
		res = Net::HTTP.start(url.host, url.port) {|http|
			http.request(req)
		}

		#This line is taking the response recieved from the backend (a json string) and parsing it into a json object.
		getUserMeetings = JSON.parse(res.body)

		#The @ makes the variable accessible by our other files
		@meetings = Array.new
		@meetingsParsed = Array.new
		meetingString = ''

		#The way the backend has it currently set up, when you ask for all the meetings attached to a user you only get the id and title. So, using this Ruby version of a loop we have to go through our newly parsed json object and make another call to the backend for each meeting the user has so that we can have all the information that needs displayed. Ideally the backend would have a call that basically does this exact same process for us because a front-end like us is meant only to display data not process it. A backend is meant to do the 'heavy-lifting'.

		#We have to specify ['meetings'] because of what the backend is sending us. They basically give us an array that contains only one element, an array of all the data we want. Why did they create this unnecessary step? No idea.

		#A java version of the loop would look like this for(Object meeting : getUserMeetings['meetings'])
		getUserMeetings['meetings'].each do |meeting|
			url = URI.parse('http://csse371-04.csse.rose-hulman.edu/Meeting/' + meeting['id'].to_s)
			req = Net::HTTP::Get.new(url.path)
			res = Net::HTTP.start(url.host, url.port) {|http|
				http.request(req)
			}
			meetingString = res.body

			#Here we are attaching the meetingID to the JSON string the backend gives us so that later in our javascript we can know what meeting we are looking at. The [0..-2] is removing the last character in the string which is the closing brace '}' that indicates the end of the json object when we parse it in the next step.
			meetingIdString = ',"meetingID":"'+meeting['id'].to_s+'"}';
			meetingString = meetingString[0..-2] + meetingIdString;

			#Here we are pushing the current meeting of the loop into our meetings array that will be available in our html and js files.
			@meetings.push(meetingString)
			
			#I had to make a parsed and a non-parsed array because of some issue with quotes not being parsed correctly in the javascript. The parsed array is used in the html file and the non-parsed is used in the js file.
			@meetingsParsed.push(JSON.parse(meetingString))
		end
	end

	layout 'slate'
end
