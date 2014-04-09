class ProfileController < ApplicationController

	def otherProfile
		if (cookies[:otherUserID].blank?)
			redirect_to '/profile/index'
			return
		end
	end
	layout 'slate'

end
