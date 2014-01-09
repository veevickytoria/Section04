class GroupsController < ApplicationController
	def index
		if (cookies[:userID].blank?)
			redirect_to '/login/index'
		end
	end

	def new
	end

	layout 'slate'
end
