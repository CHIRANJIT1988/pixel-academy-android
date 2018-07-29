package pixel.academy.alert;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import pixel.academy.helper.OnAlertButtonClick;


public class CustomAlertDialog 
{

	private Context context;
	private OnAlertButtonClick listener;
	
	
	public CustomAlertDialog(Context context, OnAlertButtonClick listener)
	{
		this.listener = listener;
		this.context = context;
	}


	public void showConfirmationDialog(String title, String message, final String action)
	{

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

		alertDialogBuilder.setMessage(message).setTitle(title).setCancelable(false) // set dialog message

				// Yes button click action
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						listener.onAlertButtonClick(true, 200, action);
					}
				})


						// No button click action
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// if this button is clicked, just close
						// the dialog box and do nothing
						dialog.cancel();
					}
				});


		AlertDialog alertDialog = alertDialogBuilder.create(); // create alert dialog

		alertDialog.show(); // show it
	}


    public void showConfirmationDialog(String message, final String action)
    {
    	
    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

		alertDialogBuilder.setMessage(message).setTitle("Not Found").setCancelable(true) // set dialog message
		
			// Yes button click action
			.setPositiveButton("CHAT", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int id) 
				{
					listener.onAlertButtonClick(true, 202, "yes");
				}
			})
				
			
			// No button click action
			.setNegativeButton("NOT NOW", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int id) 
				{
					// if this button is clicked, just close
					// the dialog box and do nothing
					dialog.cancel();
					listener.onAlertButtonClick(true, 201, action);
				}
			});

		
		AlertDialog alertDialog = alertDialogBuilder.create(); // create alert dialog

		alertDialog.show(); // show it
    }


	public void showOKDialog(String title, String message, final String action)
	{

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

		// Setting Dialog Title
		alertDialogBuilder.setTitle(title);

		alertDialogBuilder.setMessage(message).setCancelable(false) // set dialog message

				// Yes button click action
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						listener.onAlertButtonClick(true, 201, action);
					}
				});

		AlertDialog alertDialog = alertDialogBuilder.create(); // create alert dialog

		alertDialog.show(); // show it
	}


	public void showOKDialog(String title, String message)
	{

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

		// Setting Dialog Title
		alertDialogBuilder.setTitle(title);

		alertDialogBuilder.setMessage(message).setCancelable(false) // set dialog message

				// Yes button click action
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

					}
				});

		AlertDialog alertDialog = alertDialogBuilder.create(); // create alert dialog

		alertDialog.show(); // show it
	}
}