require 'net/http'
require 'base_wrapper'

class DocumentsApiWrapper < BaseWrapper

	def get_user_agendas(user_id)
		get_response(url_from_parts('/User/Agenda/', user_id.to_s))
	end

	def get_user_notes(user_id)
		get_response(url_from_parts('/User/Notes/', user_id.to_s))
	end

	def get_note(note_id)
		get_response(url_from_parts('/Note/', note_id))
	end
end

#in /web_app/lib
