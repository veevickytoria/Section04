MeetingScheduler::Application.routes.draw do
  get "dash/main"
  get "dash/newMeeting"
  get "login/login"
  get "login/resetPassword"
  get "login/about"
  get "login/signup"
  resources :requests

  resources :user_tasks

  resources :user_projects

  resources :user_meetings

  root 'login#login'
  match '/main', to: 'dash#main', via: 'get'
  match '/newMeeting', to: 'dash#newMeeting', via: 'get'
  match '/login', to: 'login#login', via: 'get'
  match '/resetPassword', to: 'login#resetPassword', via: 'get'
  match '/about', to: 'login#about', via: 'get'
  match '/signup', to: 'login#signup', via: 'get'

  # The priority is based upon order of creation: first created -> highest priority.
  # See how all your routes lay out with "rake routes".

  # You can have the root of your site routed with "root"
  # root 'welcome#index'

  # Example of regular route:
  #   get 'products/:id' => 'catalog#view'

  # Example of named route that can be invoked with purchase_url(id: product.id)
  #   get 'products/:id/purchase' => 'catalog#purchase', as: :purchase

  # Example resource route (maps HTTP verbs to controller actions automatically):
  #   resources :products

  # Example resource route with options:
  #   resources :products do
  #     member do
  #       get 'short'
  #       post 'toggle'
  #     end
  #
  #     collection do
  #       get 'sold'
  #     end
  #   end

  # Example resource route with sub-resources:
  #   resources :products do
  #     resources :comments, :sales
  #     resource :seller
  #   end

  # Example resource route with more complex sub-resources:
  #   resources :products do
  #     resources :comments
  #     resources :sales do
  #       get 'recent', on: :collection
  #     end
  #   end
  
  # Example resource route with concerns:
  #   concern :toggleable do
  #     post 'toggle'
  #   end
  #   resources :posts, concerns: :toggleable
  #   resources :photos, concerns: :toggleable

  # Example resource route within a namespace:
  #   namespace :admin do
  #     # Directs /admin/products/* to Admin::ProductsController
  #     # (app/controllers/admin/products_controller.rb)
  #     resources :products
  #   end
end
