require 'documents_api_wrapper'

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
		documents_api_wrapper = DocumentsApiWrapper.new

		@agendas = documents_api_wrapper.get_user_agendas(@userID);
		@agendasParsed = JSON.parse(@agendas)

		userAgendaIDs = Array.new
		@agendasParsed.each do |agenda|
			userAgendaIDs.push(agenda['agendaID'])
		end

		userNoteIDs = JSON.parse(documents_api_wrapper.get_user_notes(@userID))
		@notes = Array.new
		@notesParsed = Array.new

		noteString = ''

		userNoteIDs['notes'].each do |note|
			noteID = note['noteID'].to_s

			noteString = documents_api_wrapper.get_note(noteID)
			
			if !userAgendaIDs.include? note['noteID']
				@notes.push(noteString)
				@notesParsed.push(JSON.parse(noteString))
			end
		end
	end

	layout "slate"
end
