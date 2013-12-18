class ProfileController < ApplicationController
	def index
		@userTasks = {'tasks'=>[{'taskName'=>'Memo'},{'taskName'=>'Flyer'},{'taskName'=>'Drink Beer'},{'taskName'=>'Learn Japanese'},{'taskName'=>'Code Website'}]}
	end
	def editmyprofile 
	end
	layout 'slate'

end
