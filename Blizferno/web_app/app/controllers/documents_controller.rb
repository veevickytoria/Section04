class DocumentsController < ApplicationController
	def index
		@notes = JSON.parse('{"notes": [{"noteID":"1","title":"@titleText","description":"@description","content":"@contentText","dateCreated":"@dateCreated"},
			{"noteID":"2","title":"@titleText","description":"@description","content":"@contentText","dateCreated":"@dateCreated"},
			{"noteID":"3","title":"@titleText","description":"@description","content":"@contentText","dateCreated":"@dateCreated"}]}')

		@agendas = JSON.parse('{"agendas":[{"agendaID":"1","title": "agenda1","content": [{"topic": "@agendaTopic","time": "@duration","description": "@description","subtopic": [{"topic": "agendaTopic","time": "@duration","description": "@description"}]}]},
			{"agendaID":"2","title": "agenda2","content": [{"topic": "@agendaTopic","time": "@duration","description": "@description","subtopic": [{"topic": "agendaTopic","time": "@duration","description": "@description"}]}]}]}')

	end
	layout "slate"
end
