class DocumentsController < ApplicationController
	def index
		@notes = JSON.parse('{"notes": [{"noteID":"1","title":"title1","description":"@description","content":"content1","dateCreated":"@dateCreated"},
			{"noteID":"2","title":"title2","description":"@description","content":"content2","dateCreated":"@dateCreated"},
			{"noteID":"3","title":"title3","description":"@description","content":"content3","dateCreated":"@dateCreated"}]}')

		@agendas = JSON.parse('{"agendas":[{"agendaID":"1","title": "agenda1","content": [{"topic": "@agendaTopic","time": "@duration","description": "@description","subtopic": [{"topic": "agendaTopic","time": "@duration","description": "@description"}]}]},
			{"agendaID":"2","title": "agenda2","content": [{"topic": "@agendaTopic","time": "@duration","description": "@description","subtopic": [{"topic": "agendaTopic","time": "@duration","description": "@description"}]}]}]}')

	end
	layout "slate"
end
