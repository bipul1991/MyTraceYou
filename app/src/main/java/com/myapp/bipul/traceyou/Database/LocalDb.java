package com.myapp.bipul.traceyou.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Mobotics on 2/24/2018.
 */

public class LocalDb extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "track_my_ass";
    public static final String FRIEND_TABLE = "friend_list_tbl";
   /* public static final String FR_PHON = "FR_PHON";
    public static final String FR_NAME = "FR_NAME";
   // public static final String FR_STATUS = "FR_STATUS";
    public static final String FR_IMG = "FR_IMG";
    public static final String FR_BLOCK = "FR_BLOCK";*/

    public static final String FRIEND_REQUEST_TABLE = "REQUEST_TBL";
    public static final String FRIEND_EXIEST_TABLE = "EXIEST_TBL";



    public LocalDb(Context context) {

        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("create table " + FRIEND_TABLE +" (ID INTEGER PRIMARY KEY AUTOINCREMENT,FR_PHON TEXT,FR_NAME TEXT," +
                "FR_IMG TEXT, FR_BLOCK TEXT, FR_ADDRESS TEXT)");

        sqLiteDatabase.execSQL("create table " + FRIEND_REQUEST_TABLE +" (ID INTEGER PRIMARY KEY AUTOINCREMENT,FR_PHON TEXT,FR_NAME TEXT," +
                "FR_IMG TEXT, FR_ADDRESS TEXT,FR_STATUS TEXT)");

        sqLiteDatabase.execSQL("create table " + FRIEND_EXIEST_TABLE +" (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "FR_PHON TEXT,FR_NAME TEXT, FR_BLOCK TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+FRIEND_TABLE);

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+FRIEND_REQUEST_TABLE);

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+FRIEND_EXIEST_TABLE);

        onCreate(sqLiteDatabase);


    }


    public boolean insertFriend(String F_PHON, String F_NAME,  String F_IMG,
                              String F_BLOCK, String F_ADDRESS)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        // deletedata();
        ContentValues contentValues=new ContentValues();
        contentValues.put("FR_PHON",F_PHON);
        contentValues.put("FR_NAME",F_NAME);
      //  contentValues.put("FR_STATUS",F_STATUS);
        contentValues.put("FR_IMG",F_IMG);
        contentValues.put("FR_BLOCK",F_BLOCK);
        contentValues.put("FR_ADDRESS",F_ADDRESS);


        long result=db.insert(FRIEND_TABLE,null,contentValues);

        if (result==-1)
            return false;
        else
            return true;
    }



    public Cursor getAllFrnd(){

        SQLiteDatabase db=this.getWritableDatabase();

        Cursor res=db.rawQuery("select * from "+FRIEND_TABLE,null);

        return res;
    }

    public Cursor getSingFrnd(String id)
    {
        SQLiteDatabase db=this.getWritableDatabase();

        Cursor res=db.rawQuery("select * from "+FRIEND_TABLE+" WHERE ID ='" + id + "'",null);

        return res;
    }


    public int updateFrnd(String id, String F_PHON, String F_NAME,  String F_IMG,
                          String F_BLOCK, String F_ADDRESS)
    {

        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("FR_PHON",F_PHON);
        contentValues.put("FR_NAME",F_NAME);
      //  contentValues.put("FR_STATUS",F_STATUS);
        contentValues.put("FR_IMG",F_IMG);
        contentValues.put("FR_BLOCK",F_BLOCK);
        contentValues.put("FR_ADDRESS",F_ADDRESS);

        int count=db.update(FRIEND_TABLE,contentValues,"ID='"+id+"'",null);

        return count;

    }


    public boolean frdStatusUpdate(String id, String status)
    {
        SQLiteDatabase db=this.getWritableDatabase();
       /* ContentValues contentValues=new ContentValues();
        contentValues.put("FAVORIT",favStr);*/
      //  int count=db.update(FRIEND_TABLE,contentValues,"ID='"+id+"'",null);

        Log.d("favDAtabase","..."+status);

        try {


            if (status.equalsIgnoreCase("yes")) {

                db.execSQL("UPDATE " + FRIEND_TABLE + " SET FR_BLOCK ='no' WHERE ID ='" + id + "'");

                return true;
            }
            else
                {
                db.execSQL("UPDATE " + FRIEND_TABLE + " SET FR_BLOCK ='yes' WHERE ID ='" + id + "'");

                return true;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
        finally {
            Cursor res=db.rawQuery("select FR_BLOCK from "+FRIEND_TABLE+" WHERE ID ='" + id + "'",null);
            res.moveToFirst();
            Log.d("resultFav",".."+res.getString(0));

        }


    }

    //________________________________________Firend Request Table Starts


    public boolean insertFreq(String F_PHON, String F_NAME,  String F_IMG, String F_ADDRESS, String F_STATUS)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        // deletedata();
        ContentValues contentValues=new ContentValues();
        contentValues.put("FR_PHON",F_PHON);
        contentValues.put("FR_NAME",F_NAME);
        //  contentValues.put("FR_STATUS",F_STATUS);
        contentValues.put("FR_IMG",F_IMG);
        contentValues.put("FR_ADDRESS",F_ADDRESS);
        contentValues.put("FR_STATUS",F_STATUS);

        long result=db.insert(FRIEND_REQUEST_TABLE,null,contentValues);

        if (result==-1)
            return false;
        else
            return true;
    }

    public Cursor getAllReq(){

        SQLiteDatabase db=this.getWritableDatabase();

        Cursor res=db.rawQuery("select * from "+FRIEND_REQUEST_TABLE,null);

        return res;
    }

   public void deleteFriendReq(String id){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from "+FRIEND_REQUEST_TABLE+" where id='"+id+"'");

    }


    public boolean getSingleFrndReq(String number)
    {
        try
        {
            SQLiteDatabase db=this.getWritableDatabase();

            Cursor res=db.rawQuery("select * from "+FRIEND_REQUEST_TABLE+" WHERE FR_PHON ='" + number + "'",null);

            if(res.getCount()>0)
                return true;
            else
                return false;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    //______________________________________________________________EXist number db

    public boolean insertExist(String F_PHON, String F_NAME)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        // deletedata();
        ContentValues contentValues=new ContentValues();
        contentValues.put("FR_PHON",F_PHON);
        contentValues.put("FR_NAME",F_NAME);
        //  contentValues.put("FR_STATUS",F_STATUS);



        long result=db.insert(FRIEND_EXIEST_TABLE,null,contentValues);


        if (result==-1)
            return false;
        else
            return true;
    }

    public boolean getExistNumber(String number)
    {
        try
        {
            SQLiteDatabase db=this.getWritableDatabase();

            Cursor res=db.rawQuery("select * from "+FRIEND_EXIEST_TABLE+" WHERE FR_PHON ='" + number + "'",null);

           if(res.getCount()>0)
               return true;
           else
               return false;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    public Cursor getAllExistNumber()
    {
        SQLiteDatabase db=this.getWritableDatabase();

        Cursor res=db.rawQuery("select * from "+FRIEND_EXIEST_TABLE,null);
        return res;
    }
}
