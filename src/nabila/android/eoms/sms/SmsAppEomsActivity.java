package nabila.android.eoms.sms;

import java.util.ArrayList;

import nabila.android.eoms.sms.AddContactActivity;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SmsAppEomsActivity extends Activity {
	private Bundle b;
	private TextView tv_contacts;
	private EditText et_message;
	//private boolean smsSentStatus = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	public void onClickAddContact(View v) {
		// Toast.makeText(this, "Add Contact Button Clicked",
		// Toast.LENGTH_SHORT).show();
		Log.d("SmsAppActivity", "Trying to add contact");

		Intent i = new Intent(getApplicationContext(), AddContactActivity.class);

		Bundle b_new = new Bundle();

		if (b != null) {
			if (b.getString("selectionIds") != null
					&& b.getString("selectionIds").trim() != "") {
				b_new.putString("selectionIds", b.getString("selectionIds"));
			}
			if (b.getString("unsentContacts") != null
					&& b.getString("unsentContacts").trim() != "") {
				b_new.putString("unsentContacts", b.getString("unsentContacts"));
			}
		}
		if (!(et_message.getText().toString())
				.equals(getString(R.string.empty))) {
			// Toast.makeText(this, "birokti", Toast.LENGTH_SHORT).show();
			b_new.putString("msgBody", et_message.getText().toString());
		}
		i.putExtras(b_new);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);

	}

	public void onClickSendMessage(View v) {
		// Toast.makeText(SmsAppEomsActivity.this,
		// tv_contacts.getText().toString()+" : "+R.string.no_contacts,
		// Toast.LENGTH_SHORT).show();
		if (tv_contacts.getText() == null
				|| (tv_contacts.getText().toString())
						.equals(getString(R.string.no_contacts))) {
			Toast.makeText(SmsAppEomsActivity.this, "Please Add Recipients!",
					Toast.LENGTH_SHORT).show();
		} else if (et_message.getText() == null
				|| et_message.getText().toString()
						.equals(getString(R.string.empty))
				|| et_message.getText().toString().trim().equals("")) {
			Toast.makeText(SmsAppEomsActivity.this, "Please Add Message!",
					Toast.LENGTH_SHORT).show();
			et_message.setText(getString(R.string.empty));
		} else {
			String allRecipients = tv_contacts.getText().toString().trim();
			allRecipients = allRecipients.replace(" ", "");

			String[] recipients = allRecipients.split(";");
			String msg = et_message.getText().toString().trim();
			boolean allSent = true;

			for (String r : recipients) {
				try {
					mySmsSender(r, msg);
					// if (smsSentStatus) {
					allRecipients = allRecipients.replace(r + ";", "").trim();
					// }
					// Toast.makeText(SmsAppEomsActivity.this, allRecipients,
					// Toast.LENGTH_SHORT);
					tv_contacts.setText(allRecipients);
					// Toast.makeText(SmsAppEomsActivity.this,
					// "Success! Sent to: " + r, Toast.LENGTH_SHORT)
					// .show();
				} catch (Exception e) {
					Toast.makeText(SmsAppEomsActivity.this,
							"Error! Not sent to: " + r, Toast.LENGTH_SHORT)
							.show();
					allSent = false;
				}
			}

			if (allSent) {
				tv_contacts.setText(getString(R.string.no_contacts));
				et_message.setText(getString(R.string.empty),
						TextView.BufferType.EDITABLE);
				b.putString("selectionIds", "");
				b.putString("unsentContacts", "");
			} else {
				String unsentContacts = tv_contacts.getText().toString();
				b.putString("unsentContacts", unsentContacts);
			}
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		Log.d("AppCrash", "OnResume");

		tv_contacts = (TextView) findViewById(R.id.txt_contacts);
		et_message = (EditText) findViewById(R.id.txt_message);
		b = getIntent().getExtras();

		if (b != null) {
			if (b.getString("contacts") != null
					&& b.getString("contacts").trim() != "") {
				String contacts = b.getString("contacts");
				tv_contacts.setText(contacts);
			}
			if (b.getString("msgBody") != null
					&& !b.getString("msgBody").trim().equals("") && !b.getString("msgBody").equals(R.string.empty)) {
				String msgBody = b.getString("msgBody");
				et_message.setText(msgBody, TextView.BufferType.EDITABLE);
			}
		}
		/*
		 * else{ tv_contacts.setText(getString(R.string.no_contacts)); }
		 */
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// super.onBackPressed(); //this finishes my activity -_-
		Log.d("retention", "onBackPressed Called");
		Intent setIntent = new Intent(Intent.ACTION_MAIN);
		setIntent.addCategory(Intent.CATEGORY_HOME);
		setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(setIntent);
	}
/*
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		Log.d("retention", "onRestoreInstanceState");

		CharSequence msgBody = savedInstanceState.getCharSequence("msgBody");
		et_message.setText(msgBody.toString());

	}

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d("retention", "onSaveInstanceState");

		CharSequence msgBody = et_message.getText();
		outState.putCharSequence("msgBody", msgBody);

	}
*/
	public void mySmsSender(String phoneNumber, String smsBody) {
		String SMS_SENT = "SMS_SENT";
		String SMS_DELIVERED = "SMS_DELIVERED";

		ArrayList<PendingIntent> sentPendingIntents = new ArrayList<PendingIntent>();
		ArrayList<PendingIntent> deliveredPendingIntents = new ArrayList<PendingIntent>();

		PendingIntent sentPendingIntent = PendingIntent.getBroadcast(this, 0,
				new Intent(SMS_SENT), 0);
		PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(this,
				0, new Intent(SMS_DELIVERED), 0);
		try {
			SmsManager sms = SmsManager.getDefault();
			ArrayList<String> mSMSMessage = sms.divideMessage(smsBody);
			for (int i = 0; i < mSMSMessage.size(); i++) {
				sentPendingIntents.add(i, sentPendingIntent);
				deliveredPendingIntents.add(i, deliveredPendingIntent);
			}
			sms.sendMultipartTextMessage(phoneNumber, null, mSMSMessage,
					sentPendingIntents, deliveredPendingIntents);

		} catch (Exception e) {

			// e.printStackTrace();
			Toast.makeText(this, "SMS sending failed for "+phoneNumber,
					Toast.LENGTH_SHORT).show();
		}
		// For when the SMS has been sent
		registerReceiver(new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(context, "SMS sent successfully",
							Toast.LENGTH_SHORT).show();
					//smsSentStatus = true;
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					Toast.makeText(context, "Generic failure cause",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					Toast.makeText(context, "Service is currently unavailable",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					Toast.makeText(context, "No pdu provided",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					Toast.makeText(context, "Radio was explicitly turned off",
							Toast.LENGTH_SHORT).show();
					break;
				}

			}
		}, new IntentFilter(SMS_SENT));

		// For when the SMS has been delivered
		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(getBaseContext(), "SMS delivered",
							Toast.LENGTH_SHORT).show();
					break;
				case Activity.RESULT_CANCELED:
					Toast.makeText(getBaseContext(), "SMS not delivered",
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}, new IntentFilter(SMS_DELIVERED));

	}
}