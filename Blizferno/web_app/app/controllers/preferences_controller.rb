class PreferencesController < ApplicationController
  def home
  	if (cookies[:userID].blank?)
		redirect_to '/login/index'
		return
	end
  end
  layout 'slate'
end
