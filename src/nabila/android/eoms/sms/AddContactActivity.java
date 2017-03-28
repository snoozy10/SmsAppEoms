package nabila.android.eoms.sms;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;

import java.util.List;



public class AddContactActivity extends Activity {

    //private TextView _ScrollScreen;
	private String selectionIds = "";
	private LinearLayout _ScrollLayout;
    private DbHelper myDb;
    private String msgBody = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addcontact);        
        
        Log.d("AddContactActivity","onCreate AddContactActivity, trying to get my contacts view and DB " );
        //_ScrollScreen = (TextView)findViewById(R.id.contacts);
        _ScrollLayout = (LinearLayout)findViewById(R.id.selectcontacts);
        myDb = new DbHelper(this);
        Log.d("AddContactActivity","onCreate AddContactActivity, got my contacts view and DB " );
    }


    @Override
    protected void onResume(){
        // TODO Auto-generated method stub
        super.onResume();
        Log.d("AddContactActivity","onResume AddContactActivity" );
        
        Bundle b_new = getIntent().getExtras();
        if(b_new!=null){
        	if(b_new.getString("unsentContacts")!=null && b_new.getString("unsentContacts")!=""){
        		String[] unsentContacts = b_new.getString("unsentContacts").split(";");
        		selectionIds = "";
        		for(String c: unsentContacts){
        			selectionIds += myDb.getContactId(c);
        		}
        		b_new.putString("selectionIds", selectionIds);
        		
        	}
        	else if(b_new.getString("selectionIds")!=null && b_new.getString("selectionIds")!=""){
	    		selectionIds = b_new.getString("selectionIds");
	    		//Toast.makeText(AddContactActivity.this, selectionIds, Toast.LENGTH_SHORT).show();
	    	}
	    	if(b_new.getString("msgBody")!=null && b_new.getString("msgBody")!=""){
	    		msgBody = b_new.getString("msgBody");
	    	}
        }
        
        /*
        if(myDb.countRows()>0){
        	Log.d("AddContactActivity","Row paise, delete korbo ~.~" );
        	myDb.deleteAllContacts();
        	myDb.insertDummies();
        }
        else */if(myDb.countRows()==0){
        	Log.d("AddContactActivity","Row Paynai table-e" );
        	myDb.insertDummies();
        }

        List<String> contacts =  myDb.getContacts();
        
        View.OnClickListener checkBoxClickHandler = new OnClickListener() {
            public void onClick(View v) {
            	//Toast.makeText(AddContactActivity.this, "CheckBox "+v.getId()+" Clicked", Toast.LENGTH_SHORT).show();
            	CheckBox cb = (CheckBox) v;
            	if(cb.isChecked()){
            		selectionIds += (v.getId()+";");
            	}else{
            		selectionIds = selectionIds.replaceAll(v.getId()+";", "");
            	}
            }
        };
        

        for(String s : contacts) {
        	String[] parts = s.split(";");
        	
            //contactView += ( "ID: "+parts[0]+"\nName: "+parts[1]+"\nPhone Number: "+parts[2]+"\n\n" );
        	//contactView += ( "Name: "+parts[0] );
        	
            CheckBox cb = new CheckBox(this);
            	cb.setText("Name: "+parts[1]+"\nPhone Number: "+parts[2]);
            	cb.setId(Integer.parseInt(parts[0]));
            	cb.setOnClickListener(checkBoxClickHandler);
            	//if(true){
            	if(selectionIds.contains(parts[0]+";")){
            		cb.setChecked(true);
            	}
            _ScrollLayout.addView(cb);
        	
            
        }
        /*
        if(contactView!="")
            _ScrollScreen.setText(contactView);
        */

    }

    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//myDb.dropTable();
		myDb.close();
	}

    //to get back to sms main. bound to: done button
	public void onClickBack(View v){
        //Toast.makeText(this, "Done Clicked", Toast.LENGTH_SHORT).show();
		Intent i = new Intent(AddContactActivity.this, SmsAppEomsActivity.class);
		Bundle b = new Bundle();
		String contacts = "";
		
		if(selectionIds != ""){
			contacts = myDb.getContactsByIdString(selectionIds);
			b.putString("selectionIds", selectionIds);
		}
		if(contacts!=""){
			b.putString("contacts", contacts); 
		}
		b.putString("msgBody", msgBody);
		i.putExtras(b); 
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }


	//unused
	public void onClickDelete(View v){
		myDb.dropTable();
	}
	//unused
    public void onClickClearContacts(View v){
    	AlertDialog.Builder builder1 = new AlertDialog.Builder(v.getContext());
    	builder1.setMessage("Are you sure you want to delete all contacts?");
    	builder1.setCancelable(true);

    	builder1.setPositiveButton(
    	    "Yes",
    	    new DialogInterface.OnClickListener() {
    	        public void onClick(DialogInterface dialog, int id) {
    	            myDb.deleteAllContacts();
    	            myDb.close();
    	            
    	            
    	            Intent intent = getIntent();
    	            finish();
    	            startActivity(intent);
    	            
    	        	dialog.cancel();
    	        }
    	    });

    	builder1.setNegativeButton(
    	    "No",
    	    new DialogInterface.OnClickListener() {
    	        public void onClick(DialogInterface dialog, int id) {
    	            dialog.cancel();
    	        }
    	    });

    	AlertDialog alert11 = builder1.create();
    	alert11.show();

        //Toast.makeText(AddContactActivity.this, "Contacts test", Toast.LENGTH_SHORT).show();
    }
}
