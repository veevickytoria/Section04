class PreferencesController < ApplicationController

before_filter :home
before_filter :getUserSettings

  def home
  	if (cookies[:userID].blank?)
		redirect_to '/login/index'
		return
	end
  end

  def getUserSettings
    if (!cookies[:userID].blank?)
      require 'net/http'
      @userID = cookies[:userID]

      url = URI.parse('http://csse371-04.csse.rose-hulman.edu/UserSettings/' + @userID)
      req = Net::HTTP::Get.new(url.path)
      res = Net::HTTP.start(url.host, url.port) {|http|
        http.request(req)
      }

      settings = JSON.parse(res.body);

      @curTask = settings['tasks']
      @curGroup = settings['groups']
      @curProject = settings['projects']
      @curMeeting = settings['meetings']
    end
  end

  layout 'slate'
end
