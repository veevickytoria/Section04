class ApplicationController < ActionController::Base
  # Prevent CSRF attacks by raising an exception.
  # For APIs, you may want to use :null_session instead.
  protect_from_forgery with: :exception
  layout 'slate'

  before_filter :protect

  def protect
  	@ips = ['127.0.0.1'] #and so on...]
  	if not @ips.include? request.remote_ip
  		# check for your subnet stuff here, for example
  		# if not request.remote_ip.include?('127.0,0')
  		render text: "You Are Unauthorized"
  		return
  	end
  end
end
