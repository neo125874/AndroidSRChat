package chat.signalr.android.com.androidsrchat;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tw4585 on 2015/12/1.
 */
public class MyDBHelper extends SQLiteOpenHelper {
    private final static String _DBName = "ChatMsg.db";  //<-- db name
    private final static int _DBVersion = 1; //<-- 版本
    private final static String _TableName = "ChatBody"; //<-- table name

    // 資料庫物件，固定的欄位變數
    private static SQLiteDatabase database;

    public MyDBHelper(Context context/*, String name, CursorFactory factory,
                      int version*/) {
        //super(context, name, factory, version);
        super(context, _DBName, null, _DBVersion);
    }

    // 需要資料庫的元件呼叫這個方法，這個方法在一般的應用都不需要修改
    public static SQLiteDatabase getDatabase(Context context) {
        if (database == null || !database.isOpen()) {
            database = new MyDBHelper(context).getWritableDatabase();
        }

        return database;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String DATABASE_CREATE_TABLE =
                "create table " + _TableName + ChatMsgHelper.CREATE_TABLE;
        db.execSQL(DATABASE_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //oldVersion=舊的資料庫版本；newVersion=新的資料庫版本

        if (newVersion > oldVersion) {
            db.beginTransaction();//建立交易

            boolean success = false;//判斷參數

            //由之前不用的版本，可做不同的動作
            switch (oldVersion) {
                case 1:
                    //db.execSQL("ALTER TABLE newMemorandum ADD COLUMN reminder integer DEFAULT 0");
                    //db.execSQL("ALTER TABLE newMemorandum ADD COLUMN type VARCHAR");
                    //db.execSQL("ALTER TABLE newMemorandum ADD COLUMN memo VARCHAR");
                    oldVersion++;

                    success = true;
                    break;
            }

            if (success) {
                db.setTransactionSuccessful();//正確交易才成功
            }
            db.endTransaction();
        }
        else {
            onCreate(db);
        }
    }
}
