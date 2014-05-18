package com.meetingninja.csse.extras;

import com.meetingninja.csse.ApplicationController;

import com.meetingninja.csse.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class AlertDialogUtil {
	public static void displayDialog(Context context, String title,String message, String buttonText,DialogInterface.OnClickListener positiveListener) {
		new AlertDialog.Builder(context).setTitle(title).setMessage(message).setPositiveButton(buttonText, positiveListener).show();
	}

	public static void deleteDialog(Context context, CharSequence objectName,DialogInterface.OnClickListener positiveListener) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

		// Setting Dialog Message
		alertDialog.setMessage(ApplicationController.getInstance().getResString(R.string.prompt_delete,	objectName.toString().toLowerCase()));

		// Setting Positive "Yes" Button
		alertDialog.setPositiveButton("Yes", positiveListener);

		// Setting Negative "NO" Button
		alertDialog.setNegativeButton("No", null);
		
		// Showing Alert Message
		alertDialog.show();
	}
	
	public static void logoutDialog(Context context, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

		// Setting Dialog Message
		alertDialog.setMessage(ApplicationController.getInstance().getResString(R.string.prompt_logout));

		// Setting Positive "Yes" Button
		alertDialog.setPositiveButton("Yes", positiveListener);

		// Setting Negative "NO" Button
		alertDialog.setNegativeButton("No", negativeListener);
		
		// Showing Alert Message
		alertDialog.show();
	}

	public static void showErrorDialog(Context context, String message) {
		displayDialog(context, "Error", message, "OK",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
	}

	public static void showTwoOptionsDialog(Context context, String title,
			String message, String posButton, String negButton,
			DialogInterface.OnClickListener positiveListener,
			DialogInterface.OnClickListener negativeListener) {
		new AlertDialog.Builder(context).setTitle(title).setMessage(message)
				.setPositiveButton(posButton, positiveListener)
				.setNegativeButton(negButton, negativeListener).show();
	}
}
