package com.droidrage.meetingninja.user;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.droidrage.meetingninja.R;
import com.droidrage.meetingninja.R.id;
import com.droidrage.meetingninja.R.layout;
import com.droidrage.meetingninja.database.AsyncResponse;
import com.droidrage.meetingninja.database.UserDatabaseAdapter;
import com.droidrage.meetingninja.meetings.MeetingFetcherTask;
import com.droidrage.meetingninja.meetings.MeetingItemAdapter;

import objects.Meeting;
import objects.User;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.QuickContactBadge;
import android.widget.TextView;

public class ViewProfileFragment extends Fragment implements
		AsyncResponse<List<Meeting>> {

	private ListView meetingList;
	private List<Meeting> meetings = new ArrayList<Meeting>();
	private SessionManager session;
	private MeetingItemAdapter adpt;
	private User user;

	private TextView company, jobTitle, profileName, phoneNum, email;
	private QuickContactBadge qcb;

	private RetUserObj infoFetcher = null;
//	private GetProfBitmap bitmapFetcher = null;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		session = new SessionManager(getActivity().getApplicationContext());

		View v = inflater.inflate(R.layout.fragment_view_profile, container,
				false);
//		user = new User(session.getUserDetails().get(session.USERID));
		infoFetcher = new RetUserObj();
		infoFetcher.execute(session.getUserDetails().get(session.USERID));
//		bitmapFetcher = new GetProfBitmap();

		setupViews(v);

		meetingList = (ListView) v.findViewById(R.id.profile_meetingList);
		adpt = new MeetingItemAdapter(getActivity(), R.layout.meeting_item,
				meetings);
		meetingList.setAdapter(adpt);

		MeetingFetcherTask fetcher = new MeetingFetcherTask(this);
		fetcher.execute(session.getUserDetails().get(SessionManager.USER));

		return v;

	}

	private void setupViews(View v) {
		profileName = (TextView) v.findViewById(R.id.profile_name);
		company = (TextView) v.findViewById(R.id.company);
		jobTitle = (TextView) v.findViewById(R.id.jobtitle);
		phoneNum = (TextView) v.findViewById(R.id.profile_phone);
		email = (TextView) v.findViewById(R.id.profile_email);
		
		qcb = (QuickContactBadge) v
				.findViewById(R.id.view_prof_pic);

	}

	@Override
	public void processFinish(List<Meeting> result) {
		adpt.clear();
		adpt.addAll(result);

	}

	private void setUser(User user){
		this.user = user;
		profileName.setText(user.getDisplayName());
		company.setText(user.getCompany());
		jobTitle.setText(user.getTitle());
		phoneNum.setText(user.getPhone());
		email.setText(user.getEmail());
		qcb.setImageResource(R.drawable.joedoe);
//		bitmapFetcher.execute("http://www.tdnforums.com/uploads/profile/photo-27119.jpg?_r=0");

		
	}
	public void setProfilePic(Bitmap pic){
		this.user.setProfPic(pic);
		qcb.setImageBitmap(user.getProfPic());
	}
//	final class GetProfBitmap extends AsyncTask<String, Void, Void>{
//
//		@Override
//		protected Void doInBackground(String... params) {
//			// TODO Auto-generated method stub
////			user.setProfPic();
//			setProfilePic(getBitmapFromURL(params[0]));
////			return getBitmapFromURL(params[0]);
//			return null;
//		}
//
//		public Bitmap getBitmapFromURL(String src) {
//		    try {
//		        URL url = new URL(src);
//		        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//		        connection.setDoInput(true);
//		        connection.connect();
//		        InputStream input = connection.getInputStream();
//		        Bitmap myBitmap = BitmapFactory.decodeStream(input);
//		        return myBitmap;
//		    } catch (IOException e) {
//		    	Log.e("image   ",e.getLocalizedMessage());
//		    	return null;
//		    }
//		}
//	
//	}
	
	final class RetUserObj implements AsyncResponse<User>{


		private UserInfoFetcher infoFetcher;
		private User user;


		public RetUserObj(){
			infoFetcher = new UserInfoFetcher(this);
			user = new User();
		}

		
		public void execute(String userID){
			infoFetcher.execute(userID);
			
		}

		@Override
		public void processFinish(User result) {
			setUser(result);
		}
	}

}

