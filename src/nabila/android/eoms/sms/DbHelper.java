package nabila.android.eoms.sms;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {
    /** Called when the activity is first created. */
	private static final String DATABASE_NAME ="contacts.db";
    private static final String TABLE_NAME = "myContacts";
    private static final String KEY_ID = "ID";
    private static final String COL_2 = "NAME";
    private static final String COL_3 = "PHONENUMBER";
    private String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "( " + KEY_ID + " INTEGER PRIMARY KEY, " + COL_2 + " TEXT," + COL_3 + " TEXT" + " )";
    //private String DROP_CONTACTS_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    SQLiteDatabase db;

    public DbHelper(Context context) {

        super(context, DATABASE_NAME, null , 1);
        Log.d("dbHelpLog","Constructor called");

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("dbHelpLog","onCreate called");
       
        db.execSQL(CREATE_CONTACTS_TABLE);
        this.db = db;
        
        
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        Log.d("dbHelpLog","onUpgrade called");
        onCreate(db);
    }


    public boolean insertData(String name, String phone_no){
    	
        db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        
        Log.d("dbHelpLog","Inside Insert Data BEFORE" );
        //contentValues.put(KEY_ID, 0);
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, phone_no);
        
        Log.d("dbHelpLog","Inside Insert Data AFTER" );
        long check = db.insert(TABLE_NAME, null, contentValues);
        
        //db.close();
        
        Log.d("dbHelpLog","Insert check = "+ check );
        if(check == -1) return false;
        else return true;
    }
    public String getContactById(int id){
    	String contact = "";
    	db = getReadableDatabase();
    	
    	String query = "select "+COL_3+" from " + TABLE_NAME + " where "+KEY_ID+" = "+id;
    	Cursor cursor = db.rawQuery(query,null);
    	if (cursor!=null && cursor.moveToFirst()) {
    		contact = cursor.getString(0);
    	}
    	cursor.close();
    	return contact;
    }
    public String getContactId(String c){
    	String id = "";
    	db = getReadableDatabase();
    	
    	String query = "select "+KEY_ID+" from " + TABLE_NAME + " where "+COL_3+" = "+c;
    	Cursor cursor = db.rawQuery(query,null);
    	if (cursor!=null && cursor.moveToFirst()) {
    		id = cursor.getString(0);
    	}
    	cursor.close();
    	return id;
    }
    public String getContactByIdString(String id){
    	String contact = "";
    	db = getReadableDatabase();
    	
    	String query = "select "+COL_3+" from " + TABLE_NAME + " where "+KEY_ID+" = "+id;
    	Cursor cursor = db.rawQuery(query,null);
    	if (cursor!=null && cursor.moveToFirst()) {
    		contact = cursor.getString(0);
    	}
    	cursor.close();
    	return contact;
    }
    public String getContactsByIdString(String ids){
    	String contacts = "";
    	String[] parts = ids.split(";");
    	for(String p:parts){
    		contacts += (getContactByIdString(p)+"; ");
    	}
    	return contacts;
    }
    public List<String> getContacts(){
        db = getReadableDatabase();
        List<String> contactList = new ArrayList<String>();

        //String query = "select "+COL_2+","+COL_3+" from "+TABLE_NAME;
        String query = "select * from " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query,null);

        if (cursor!=null && cursor.moveToFirst()) {
            do {
                String data;
                //data = cursor.getString(0) + ";hello";
                data = cursor.getString(0) + ";" + cursor.getString(1) + ";" + cursor.getString(2);
                //+ ";" + cursor.getString(2);
                contactList.add(data);
            }
            while (cursor.moveToNext());
        }/*
        if (cursor!=null && cursor.moveToFirst()) {
            do {
                String data;
                data = "Name: "+cursor.getString(1)+"\nNumber: ";
                data += cursor.getString(2)+"\n\n";
                contactList.add(data);
            }
            while (cursor.moveToNext());
        }*/
        cursor.close();
        //db.close();
        return contactList;
    }

    public void deleteAllContacts(){
        db = getWritableDatabase();
        //db.execSQL("DELETE * FROM TABLE "+ TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        Log.d("dbHelpLog","Deleted all contacts");
        onCreate(db);
    }
    public long countRows(){
    	db = getReadableDatabase();
        
        long count = DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        //db.close();
        return count;
    }
    public void dropTable(){
    	db = getWritableDatabase();
    	db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
    }
    public void insertDummies(){
    	Log.d("dbHelpLog","Inside insertDummies()");
    	
    	
    	String[] seedNames = {"Nabila", "EOMS1", "EOMS2", "Dummy"};
    	String[] seedNumbers = {"01966166548", "01784222266", "01784222255", "01234567890" };
    	
    	
    	Log.d("dbHelpLog","initialized my strings");
    	for(int i=0; i<seedNames.length; i++){
       
	    	Log.d("dbHelpLog","Inside insertDummies() forLOOP");
		
    		//flag = insertData(seedNames[i], seedNumbers[i]);
    		if(!(insertData(seedNames[i], seedNumbers[i]))) {
    			Log.d("dbHelpLog","Insert error for seedName = "+ seedNames[i] +", seedNumber = " + seedNumbers[i] );
    			return;
    		}
    	
    	}
    }
}