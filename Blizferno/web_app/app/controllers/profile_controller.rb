class ProfileController < ApplicationController
	def index
		require 'net/http'
		url = URI('http://csse371-04.csse.rose-hulman.edu/User/350')
		Net::HTTP.get(url)
		@mymothafuckinemail = params[:email]
	end
	def editmyprofile 
	end
	layout 'slate'
end