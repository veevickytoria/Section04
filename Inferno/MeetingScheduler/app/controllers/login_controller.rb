class LoginController < ApplicationController
  def login
  end

  def resetPassword

  end

  def about
  end

  def signup

  end

  def create
    @user = SystemUser.new(user_params)    # Not the final implementation!
    if @user.save
      # Handle a successful save.
    else
      render 'new'
    end
  end

  private 
  	def user_params
  		params.require(:SystemUser).permit(:name, :email, :password, :password_confirmation)
  	end

end
