package ch.accountmaker.dao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

	private static String DB_NAME = "account_db";
	private final Context myContext;
	
	public static SQLiteDatabase db; 
	private static String DB_PATH = "/data/data/ch.accountmaker/databases/";
    private SQLiteDatabase myDataBase; 
	private static DatabaseOpenHelper mInstance;  
    private static boolean isDbOpen = false;

	public DatabaseOpenHelper(Context context) {
		super(context, DB_NAME, null, 1);
		this.myContext = context;

	}


	public static synchronized DatabaseOpenHelper getInstance(Context context) {  
        if (mInstance == null) {  
            mInstance = new DatabaseOpenHelper(context);  
        }  
        return mInstance;  
    }
    
    @Override
   	public void onCreate(SQLiteDatabase db) {

       	
    	}
       public void openDataBase() throws SQLException{
       	//Open the database
//           String myPath = DB_PATH + DB_NAME;
//       	db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
       	if(db == null || !isDbOpen){
   			db = this.getReadableDatabase();
   			isDbOpen = true;
   		}
       }
       @Override
   	public synchronized void close() {
        	    if(db != null)
       		    db.close();
        	    isDbOpen = false;
        	    super.close();
    	}
    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException{
    	boolean dbExist = checkDataBase();
    	if(dbExist){
    		//do nothing - database already exist
    		openDataBase();
//    		//空间位置表RoadLine
//    		String sql = "Create TABLE MAIN.[RoadLine]([Id] bigint(20) PRIMARY KEY,[parent_id] bigint(20),[type] bigint(20),[code] varchar(16),[name] varchar(128),[mile] DECIMAL(12,3),[direction] varchar(16),[grade] INTEGER(10),[waypass] varchar(128),[begin_mile] DECIMAL(12,3),[end_mile] DECIMAL(12,3),[center_mile] DECIMAL(12,3),[complete_date] DATE,[pass_date] DATE,[road_type] varchar(2),[drive_way_type] varchar(2),[drive_way_property] varchar(14),[tech_grade] varchar(2),[tollhouse_type] varchar(2),[machine_room_type] varchar(2),[monitoring_hall_type] varchar(2),[substation_type] varchar(2),[interflow_type] varchar(2),[area] varchar(256),[remark] varchar(256),[tree_code] varchar(128),[is_limit_hight] integer(6),[is_increase_width] integer(6),[tree_name] varchar(1024),[type_code] varchar(128),[lane_num] varchar(2),[lessee_id] bigint(20),[capital] varchar(256),[tunnel_type] varchar(10))";
//    		toBuildTable("RoadLine", sql);
//    		
    		
    		//
    		
    	}else{
    		//By calling this method and empty database will be created into the default system path
               //of your application so we are gonna be able to overwrite that database with our database.
        	this.getReadableDatabase();
	       	try {
	    			copyDataBase();
	    			openDataBase();
	    		} catch (IOException e) {
	         		throw new Error("Error copying database");
	         	}
	    	}
     }
    
    private boolean checkDataBase(){
 		boolean isExitDatabase = false;
     	try{
    		String myPath = DB_PATH + DB_NAME;
     		File dbFile = myContext.getDatabasePath(myPath);
     		if(dbFile.exists()){
     			isExitDatabase = true;
     		}else {
     			isExitDatabase = false;
			}
     	}catch(SQLiteException e){
     		e.printStackTrace();
     	}
     	return isExitDatabase;
}

/**
 * Copies your database from your local assets-folder to the just created empty database in the
 * system folder, from where it can be accessed and handled.
 * This is done by transfering bytestream.
 * */
private void copyDataBase() throws IOException{
 	//Open your local db as the input stream
	InputStream myInput = myContext.getAssets().open(DB_NAME);
 	// Path to the just created empty db
	String outFileName = DB_PATH + DB_NAME;
 	//Open the empty db as the output stream
	OutputStream myOutput = new FileOutputStream(outFileName);
 	//transfer bytes from the inputfile to the outputfile
	byte[] buffer = new byte[1024];
	int length;
	while ((length = myInput.read(buffer))>0){
		myOutput.write(buffer, 0, length);
	}
 	//Close the streams
	myOutput.flush();
	myOutput.close();
	myInput.close();
 }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	public ArrayList<Cursor> getData(String Query) {
		// get writable database
		SQLiteDatabase sqlDB = this.getWritableDatabase();
		String[] columns = new String[] { "mesage" };
		// an array list of cursor to save two cursors one has results from the
		// query
		// other cursor stores error message if any errors are triggered
		ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
		MatrixCursor Cursor2 = new MatrixCursor(columns);
		alc.add(null);
		alc.add(null);

		try {
			String maxQuery = Query;
			// execute the query results will be save in Cursor c
			Cursor c = sqlDB.rawQuery(maxQuery, null);

			// add value to cursor2
			Cursor2.addRow(new Object[] { "Success" });

			alc.set(1, Cursor2);
			if (null != c && c.getCount() > 0) {

				alc.set(0, c);
				c.moveToFirst();

				return alc;
			}
			return alc;
		} catch (SQLException sqlEx) {
			Log.d("printing exception", sqlEx.getMessage());
			// if any exceptions are triggered save the error message to cursor
			// an return the arraylist
			Cursor2.addRow(new Object[] { "" + sqlEx.getMessage() });
			alc.set(1, Cursor2);
			return alc;
		} catch (Exception ex) {

			Log.d("printing exception", ex.getMessage());

			// if any exceptions are triggered save the error message to cursor
			// an return the arraylist
			Cursor2.addRow(new Object[] { "" + ex.getMessage() });
			alc.set(1, Cursor2);
			return alc;
		}

	}

}
