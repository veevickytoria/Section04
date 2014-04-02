class BaseWrapper
	HOSTNAME = 'http://csse371-04.csse.rose-hulman.edu'

	def get_response(url)
		req = Net::HTTP::Get.new(url.path)
		Net::HTTP.start(url.host, url.port) {|http|
			http.request(req)
		}.body
	end

	def url_from_parts(*parts)
		URI.parse(HOSTNAME + parts.reduce('', &:+))
	end
end

#web_app/lib/