require "spec_helper"

describe UserMeetingsController do
  describe "routing" do

    it "routes to #index" do
      get("/user_meetings").should route_to("user_meetings#index")
    end

    it "routes to #new" do
      get("/user_meetings/new").should route_to("user_meetings#new")
    end

    it "routes to #show" do
      get("/user_meetings/1").should route_to("user_meetings#show", :id => "1")
    end

    it "routes to #edit" do
      get("/user_meetings/1/edit").should route_to("user_meetings#edit", :id => "1")
    end

    it "routes to #create" do
      post("/user_meetings").should route_to("user_meetings#create")
    end

    it "routes to #update" do
      put("/user_meetings/1").should route_to("user_meetings#update", :id => "1")
    end

    it "routes to #destroy" do
      delete("/user_meetings/1").should route_to("user_meetings#destroy", :id => "1")
    end

  end
end
