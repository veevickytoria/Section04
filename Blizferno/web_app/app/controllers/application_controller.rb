class ApplicationController < ActionController::Base
  # Prevent CSRF attacks by raising an exception.
  # For APIs, you may want to use :null_session instead.


  #protect_from_forgery with: :exception

  protect_from_forgery with: :null_session

  before_filter :getUserInfo
  before_filter :getNotifications
  before_filter :getAllUsers

  def getUserInfo
    if (!cookies[:userID].blank?)
      require 'net/http'
      @userID = cookies[:userID]
      url = URI.parse('http://csse371-04.csse.rose-hulman.edu/User/' + @userID)
      req = Net::HTTP::Get.new(url.path)
      res = Net::HTTP.start(url.host, url.port) {|http|
        http.request(req)
      }
      @userInfo = JSON.parse(res.body)
    end
  end

  def getNotifications
  	if(!cookies[:userID].blank?)
  		require 'net/http'
  		@userID = cookies[:userID]
  		url = URI.parse('http://csse371-04.csse.rose-hulman.edu/Notification/' + @userID)
  		req = Net::HTTP::Get.new(url.path)
  		res = Net::HTTP.start(url.host, url.port) {|http|
  			http.request(req)
  		}
  		# TODO: Put back in when database actually adds and deletes notifications
  		# @Notifications = JSON.parse(res.body)
  		@Notifications = JSON.parse('{"totalAmount":"6","userID":"0","notifications":[{"datetime":"","type":"Meeting","nodeID":"","description":""},{"datetime":"","type":"Task","nodeID":"","description":""},{"datetime":"","type":"Project","nodeID":"","description":""},{"datetime":"","type":"Task","nodeID":"","description":""},{"datetime":"","type":"Group","nodeID":"","description":""},{"datetime":"","type":"Meeting","nodeID":"","description":""},{"datetime":"","type":"Project","nodeID":"","description":""},{"datetime":"","type":"Task","nodeID":"","description":""},{"datetime":"","type":"Meeting","nodeID":"","description":""},{"datetime":"","type":"Project","nodeID":"","description":""},{"datetime":"","type":"Meeting","nodeID":"","description":""},{"datetime":"","type":"Task","nodeID":"","description":""},{"datetime":"","type":"Task","nodeID":"","description":""}]}')
  		@Noti = @Notifications["notifications"]
  		@Meeting = 0
  		@Task = 0
  		@Project = 0
  		@Group = 0	

  		@Noti.each do |i|
  			case i["type"]
  			 when "Meeting"
  			 	@Meeting+=1
  			when "Task"
  				@Task+=1
  			when "Project"
  				@Project+=1
  			when "Group"
  				@Group+=1
  			end
  		end

  		if (@Meeting == 0)
  			@Meeting = ""
  		end

  		if(@Task == 0)
  			@Task = ""
  		end

  		if (@Project == 0)
  			@Project = ""
  		end

  		if (@Group == 0)
  			@Group = ""
  		end
  	end
  end

  def getAllUsers
    require 'net/http'
    url = URI.parse('http://csse371-04.csse.rose-hulman.edu/User/Users')
    req = Net::HTTP::Get.new(url.path)
    res = Net::HTTP.start(url.host, url.port) {|http|
      http.request(req)
    }
    @allUsers = JSON.parse(res.body)
	@allUsersRaw = res.body
  end

  #before_filter :protect

  #def protect
   # roseNetworkIP = IPAddr.new '137.0.0.0/8'
    #clientIP = IPAddr.new 'xxx.xxx.xxx/X'
  	#@ips = ['127.0.0.1'] #and so on...]
    #userIP = request.remote_ip
  	#if not (@ips.include? userIP or roseNetworkIP.include? userIP)
  		# check for your subnet stuff here, for example
  		# if not request.remote_ip.include?('127.0,0')
  	#	render text: '<h1>The IP of <i>' + userIP + '</i> is unauthorized to access this section.</h1><br />Please contact system administrator if you believe you have reached this page in error.'
  	#	return
  	#end
  #end
end
