class ApplicationController < ActionController::Base
  
  protect_from_forgery with: :null_session

  before_filter :loginRedirect
  before_filter :getUserInfo
  before_filter :getNotifications
  before_filter :getAllUsers
  before_filter :getAllMeetings
  before_filter :getAllNotes
  before_filter :loadSettings

  def loginRedirect
	if (cookies[:userID].blank?)
		redirect_to '/login/index'
		return
	end
  end

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
    
      @Notifications = JSON.parse(res.body)
      # Testing string
      # @Notifications = JSON.parse('{"totalAmount":"6","userID":"0","notifications":[{"datetime":"","type":"Meeting","nodeID":"","description":""},{"datetime":"","type":"Task","nodeID":"","description":""},{"datetime":"","type":"Project","nodeID":"","description":""},{"datetime":"","type":"Task","nodeID":"","description":""},{"datetime":"","type":"Group","nodeID":"","description":""},{"datetime":"","type":"Meeting","nodeID":"","description":""},{"datetime":"","type":"Project","nodeID":"","description":""},{"datetime":"","type":"Task","nodeID":"","description":""},{"datetime":"","type":"Meeting","nodeID":"","description":""},{"datetime":"","type":"Project","nodeID":"","description":""},{"datetime":"","type":"Meeting","nodeID":"","description":""},{"datetime":"","type":"Task","nodeID":"","description":""},{"datetime":"","type":"Task","nodeID":"","description":""}]}')
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

  def getAllMeetings
    if (!cookies[:userID].blank?)
      require 'net/http'
      @userID = cookies[:userID]
      url = URI.parse('http://csse371-04.csse.rose-hulman.edu/User/Meetings/' + @userID)
      req = Net::HTTP::Get.new(url.path)
      res = Net::HTTP.start(url.host, url.port) {|http|
        http.request(req)
      }
      @userMeetings = JSON.parse(res.body)
      @userMeetingsRaw = res.body
    end
  end


  def getAllNotes
    if (!cookies[:userID].blank?)
      require 'net/http'
      @userID = cookies[:userID]
      url = URI.parse('http://csse371-04.csse.rose-hulman.edu/User/Notes/' + @userID)
      req = Net::HTTP::Get.new(url.path)
      res = Net::HTTP.start(url.host, url.port) {|http|
        http.request(req)
      }
      @userNotes = JSON.parse(res.body)
      @userNotesRaw = res.body
    end
  end

  def loadSettings
    if (!cookies[:userID].blank?)
      require 'net/http'
      @userID = cookies[:userID]
      url = URI.parse('http://csse371-04.csse.rose-hulman.edu/UserSettings/' + @userID)
      req = Net::HTTP::Get.new(url.path)
      res = Net::HTTP.start(url.host, url.port) {|http|
        http.request(req)
      }
      @settings = JSON.parse(res.body)

      if(@settings['tasks'] == "")
        @settings['tasks'] = 3;
      end
      if(@settings['groups'] == "")
        @settings['groups'] = 3;
      end
      if(@settings['projects'] == "")
        @settings['projects'] = 3;
      end
      if(@settings['meetings'] == "")
        @settings['meetings'] = 3;
      end


    end
  end

end
