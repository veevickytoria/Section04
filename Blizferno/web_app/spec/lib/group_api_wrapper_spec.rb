require 'spec_helper'
require 'group_api_wrapper'

describe GroupApiWrapper do
	let(:wrapper){
		GroupApiWrapper.new
	}

	it 'gets group summaries from the database' do
		userID = 645
		group_summaries = wrapper.get_user_groups(userID)
		groups_parsed = JSON.parse(group_summaries)
		groups_parsed['groups'].each do |group|
			group.keys.should eq ['groupID']
		end
	end

	it 'gets group information' do
		groupID = 4460.to_s
		group_string = wrapper.get_group(groupID)
		group_parsed = JSON.parse(group_string)
		group_parsed.keys.should eq ['nodeType', 'groupTitle', 'groupID', 'members']
	end
end

#web_app/spec/lib/