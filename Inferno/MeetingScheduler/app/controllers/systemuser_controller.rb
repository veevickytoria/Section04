class SystemUserController < ApplicationController
	
	def new
		@user = SystemUser.new
  	end

end