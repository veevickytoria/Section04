class ProfileController < ApplicationController
	def index
		require 'net/http'
		#url = URI.parse('http://csse371-04.csse.rose-hulman.edu/User/@userID/')
		url = URI.parse('http://csse371-04.csse.rose-hulman.edu/User/717')
		req = Net::HTTP::Get.new(url.path)
		res = Net::HTTP.start(url.host, url.port) {|http|
			http.request(req)
		}
		#@me = {'name'=>'jerry','title'=>'boss'}
		@stuff = JSON.parse(res.body)
		# http = Net::HTTP.new(uri.host, uri.port)
		# request = Net::HTTP::Get.new(uri.request_uri)
		# response = http.request(request)
	end
	def editmyprofile 
	end
	layout 'slate'

end