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

    @userID = cookies[:userID]

  	require 'net/http'

  	url = URI.parse('http://csse371-04.csse.rose-hulman.edu/UserSettings/' + @userID)
  	req = Net::HTTP::Get.new(url.path)
  	res = Net::HTTP.start(url.host, url.port){|http|http.request(req)}

    @userSettings = JSON.parse(res.body)

  end

  layout 'slate'
end
