class ProfileController < ApplicationController
	def index
		require 'net/http'
		url = URI('http://csse371-04.csse.rose-hulman.edu/User/350')
		params = { :email => 10}
		Net::HTTP.get(url)

	end
	def editmyprofile 
	end
	layout 'slate'

end