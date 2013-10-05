# encoding: UTF-8
# This file is auto-generated from the current state of the database. Instead
# of editing this file, please use the migrations feature of Active Record to
# incrementally modify your database, and then regenerate this schema definition.
#
# Note that this schema.rb definition is the authoritative source for your
# database schema. If you need to create the application database on another
# system, you should be using db:schema:load, not running all the migrations
# from scratch. The latter is a flawed and unsustainable approach (the more migrations
# you'll amass, the slower it'll run and the greater likelihood for issues).
#
# It's strongly recommended that you check this file into your version control system.

ActiveRecord::Schema.define(version: 20131004132624) do

  create_table "meetings", force: true do |t|
    t.string   "Title"
    t.string   "Description"
    t.date     "PreferreredDate"
    t.date     "ActualDate"
    t.integer  "project_id"
    t.time     "StartTime"
    t.float    "Duration"
    t.string   "Location"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "projects", force: true do |t|
    t.string   "Name"
    t.string   "Description"
    t.string   "ClientName"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "requests", force: true do |t|
    t.integer  "user_id"
    t.integer  "meeting_id"
    t.boolean  "Accepted"
    t.boolean  "Responded"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "system_users", force: true do |t|
    t.string   "name"
    t.string   "email"
    t.string   "password"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "tasks", force: true do |t|
    t.string   "Title"
    t.string   "Description"
    t.date     "DateDue"
    t.datetime "DateStarted"
    t.datetime "DateCompleted"
    t.integer  "project_id"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "user_meetings", force: true do |t|
    t.integer  "user_id"
    t.integer  "meeting_id"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "user_projects", force: true do |t|
    t.integer  "user_id"
    t.integer  "project_id"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "user_tasks", force: true do |t|
    t.integer  "user_id"
    t.integer  "task_id"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "users", force: true do |t|
    t.string   "FirstName"
    t.string   "LastName"
    t.string   "Email"
    t.string   "Password"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

end
