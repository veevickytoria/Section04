class DocumentsController < ApplicationController

before_filter :index
before_filter :getDocuments

	def index
		if (cookies[:userID].blank?)
			redirect_to '/login/index'
			return
		end
	end

	def getDocuments

		require 'net/http'
		userID = cookies[:userID]

		url = URI.parse('http://csse371-04.csse.rose-hulman.edu/User/Agenda/' + userID)
		req = Net::HTTP::Get.new(url.path)
		res = Net::HTTP.start(url.host, url.port) {|http|
			http.request(req)
		}

		@agendas = res.body
		@agendasParsed = JSON.parse(res.body)

		userAgendaIDs = Array.new
		@agendasParsed.each do |agenda|
			userAgendaIDs.push(agenda['agendaID'])
		end



		url = URI.parse('http://csse371-04.csse.rose-hulman.edu/User/Notes/' + userID)
		req = Net::HTTP::Get.new(url.path)
		res = Net::HTTP.start(url.host, url.port) {|http|
			http.request(req)
		}

		userNoteIDs = JSON.parse(res.body)
		@notes = Array.new
		@notesParsed = Array.new

		noteString = ''

		userNoteIDs['notes'].each do |note|
			url = URI.parse('http://csse371-04.csse.rose-hulman.edu/Note/' + note['noteID'].to_s)
			req = Net::HTTP::Get.new(url.path)
			res = Net::HTTP.start(url.host, url.port) {|http|
				http.request(req)
			}
			noteString = res.body
			if !userAgendaIDs.include? note['noteID']
				@notes.push(noteString)
				@notesParsed.push(JSON.parse(noteString))
			end
		end
	end

	layout "slate"
end
