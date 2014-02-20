class DocumentsController < ApplicationController

before_filter :index
before_filter :getDocuments

	def index

		 @agendas = JSON.parse('{"agendas":[{"agendaID":"1","title": "agenda1","content": [{"topic": "@agendaTopic","time": "@duration","description": "@description","subtopic": [{"topic": "agendaTopic","time": "@duration","description": "@description"}]}]},
		 	{"agendaID":"2","title": "agenda2","content": [{"topic": "@agendaTopic","time": "@duration","description": "@description","subtopic": [{"topic": "agendaTopic","time": "@duration","description": "@description"}]}]}]}')


		if (cookies[:userID].blank?)
			redirect_to '/login/index'
			return
		end
	end

	def getDocuments

		require 'net/http'
		@userID = cookies[:userID]

		url = URI.parse('http://csse371-04.csse.rose-hulman.edu/User/Notes/' + @userID)
		req = Net::HTTP::Get.new(url.path)
		res = Net::HTTP.start(url.host, url.port) {|http|
			http.request(req)
		}

		@UserNoteIDs = JSON.parse(res.body)
		@notes = Array.new
		@notesParsed = Array.new

		noteString = ''

		@UserNoteIDs['notes'].each do |note|
			url = URI.parse('http://csse371-04.csse.rose-hulman.edu/Note/' + note['noteID'].to_s)
			req = Net::HTTP::Get.new(url.path)
			res = Net::HTTP.start(url.host, url.port) {|http|
				http.request(req)
			}
			noteString = res.body

			@notes.push(noteString)
			@notesParsed.push(JSON.parse(noteString))

		end
	end
	layout "slate"
end
