class ApplicationController < ActionController::Base
  # Prevent CSRF attacks by raising an exception.
  # For APIs, you may want to use :null_session instead.
  protect_from_forgery with: :exception
  layout 'slate'

  before_filter :protect

  def protect
    roseNetworkIP = IPAddr.new '137.0.0.0/8'
    #clientIP = IPAddr.new 'xxx.xxx.xxx/X'
  	@ips = ['127.0.0.1'] #and so on...]
    userIP = request.remote_ip
  	if not (@ips.include? userIP or roseNetworkIP.include? userIP)
  		# check for your subnet stuff here, for example
  		# if not request.remote_ip.include?('127.0,0')
  		render text: '<h1>The IP of <i>' + userIP + '</i> is unauthorized to access this section.</h1><br />Please contact system administrator if you believe you have reached this page in error.'
  		return
  	end
  end
end
