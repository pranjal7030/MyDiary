package jain.pranjal.mydiary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hp on 9/28/2019.
 */

public class DatabaseYear extends SQLiteOpenHelper {

    private static String db_name = "databaseYear_database";
    private static String tbl_name ="databaseYear_table";

    private static String create_record = "create table " + tbl_name + "(ID INTEGER PRIMARY KEY AUTOINCREMENT ,YEAR TEXT );";
    private static String drop_record = "drop table if exist" + tbl_name;

    Context context;

    public DatabaseYear(Context context) {
        super(context, db_name, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(create_record);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(drop_record);
        onCreate(db);

    }

    public long insertMain(String year)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put("YEAR",year);
        long l=db.insert(tbl_name,null,cv);
        db.close();
        return l;
    }


    public Cursor getData()
    {
        SQLiteDatabase sqdb = this.getWritableDatabase();
        //String orderBy="ID desc";
        // Cursor c = sqdb.rawQuery("select * from "+tbl_name, null);
        Cursor data= sqdb.query(tbl_name,null,null,null,null,null,null,null);
        return data;

    }

    public boolean deleteIt(String nm[])
    {
        SQLiteDatabase dba=this.getWritableDatabase();

        long l =dba.delete(tbl_name,"YEAR=?",nm);
        if(l == -1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }


    }



