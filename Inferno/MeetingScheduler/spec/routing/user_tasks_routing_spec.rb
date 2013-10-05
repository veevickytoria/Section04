require "spec_helper"

describe UserTasksController do
  describe "routing" do

    it "routes to #index" do
      get("/user_tasks").should route_to("user_tasks#index")
    end

    it "routes to #new" do
      get("/user_tasks/new").should route_to("user_tasks#new")
    end

    it "routes to #show" do
      get("/user_tasks/1").should route_to("user_tasks#show", :id => "1")
    end

    it "routes to #edit" do
      get("/user_tasks/1/edit").should route_to("user_tasks#edit", :id => "1")
    end

    it "routes to #create" do
      post("/user_tasks").should route_to("user_tasks#create")
    end

    it "routes to #update" do
      put("/user_tasks/1").should route_to("user_tasks#update", :id => "1")
    end

    it "routes to #destroy" do
      delete("/user_tasks/1").should route_to("user_tasks#destroy", :id => "1")
    end

  end
end
