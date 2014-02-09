class ProjectsController < ApplicationController

	before_filter :getProjects

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

  def getProjects
  	require 'net/http'
	url = URI.parse('http://csse371-04.csse.rose-hulman.edu/User/Projects/' + @userID)
	req = Net::HTTP::Get.new(url.path)
	res = Net::HTTP.start(url.host, url.port) {|http|
		http.request(req)
	}
	logger.debug "the user id #{@userID}"
	logger.debug "check the res #{res.body}"
	getUserProjects = JSON.parse(res.body)
	@projects = Array.new
	@projectsParsed = Array.new
	@projectString = ''
	logger.info "Let's start here..."
	logger.debug "Check the projects #{getUserProjects}"
	getUserProjects['projects'].each do |project|
		url = URI.parse('http://csse371-04.csse.rose-hulman.edu/Project' + project['id'].to_s)
		req = Net::HTTP::Get.new(url.path)
		res = Net::HTTP.start(url.host, url.port) {|http|
			http.request(req)
		}
		@projectString = res.body
		projectIdString = ',"projectID":"'+project['id'].to_s+'"}';
		@projectString = @projectString[0..-2] + projectIdString;
		@projects.push(@projectString)
		@projectsParsed.push(JSON.parse(@projectString))
		logger.info "Loop..."
		logger.debug "Check the string: #{@projectsParsed.attributes.inspect}"
	end

  end
  layout 'slate'
end
