class ProjectsController < ApplicationController
  def index
  	if (cookies[:userID].blank?)
			redirect_to '/login/index'
			return
		end
		require 'net/http'
		@UserID = '717'
		url = URI.parse('http://csse371-04.csse.rose-hulman.edu/User/Tasks/' + @UserID)
		req = Net::HTTP::Get.new(url.path)
		res = Net::HTTP.start(url.host, url.port) {|http|
			http.request(req)
		}

		@projects = JSON.parse('{"projects":[{"projectID":"1","name":"Project Uno","group":"Web"},
											 {"projectID":"2","name":"Project Dos","group":"Backend"},
											 {"projectID":"3","name":"Project Tres","group":"Android"},
											 {"projectID":"4","name":"Project Cuatro","group":"iOS"}]}')

  end

  def list
  end

  def new
  end
  layout 'slate'
end
