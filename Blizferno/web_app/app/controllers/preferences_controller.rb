require 'preferences_api_wrapper'

class PreferencesController < ApplicationController

before_filter :home
before_filter :getUserSettings

  def home
  	if (cookies[:userID].blank?)
		redirect_to '/login/index'
		return
	end

  def getUserSettings
      preferences_api_wrapper = PreferencesApiWrapper.new

      settings = JSON.parse(preferences_api_wrapper.get_user_preferences(@userID))

      @curTask = settings['tasks']
      @curGroup = settings['groups']
      @curProject = settings['projects']
      @curMeeting = settings['meetings']
    end
  end

  layout 'slate'
end
