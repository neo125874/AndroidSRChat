package chat.signalr.android.com.androidsrchat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by tw4585 on 2015/12/1.
 */
public class ChatMsgHelper {
    // 編號表格欄位名稱，固定不變
    public static final String KEY_ID = "_id";

    // 其它表格欄位名稱
    public static final String isMe_COLUMN = "isMe";
    public static final String message_COLUMN = "message";
    public static final String userId_COLUMN = "userId";
    public static final String dateTime_COLUMN = "dateTime";
    public static final String withName_COLUMN = "withName";

    // 使用上面宣告的變數建立表格的SQL指令
    public static final String CREATE_TABLE =
            " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + isMe_COLUMN + " INTEGER NOT NULL, "// 0: false, 1: true
            + message_COLUMN + " TEXT NOT NULL, "
            + userId_COLUMN + " TEXT, "
            + dateTime_COLUMN + " DATETIME NOT NULL, "
            + withName_COLUMN + " TEXT NOT NULL)";

    // 資料庫物件
    private SQLiteDatabase db;

    // 建構子，一般的應用都不需要修改
    public ChatMsgHelper(Context context) {
        db = MyDBHelper.getDatabase(context);
    }

    // 關閉資料庫，一般的應用都不需要修改
    public void close() {
        db.close();
    }

    // 新增參數指定的物件
    public void insert(ChatMessage item) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(isMe_COLUMN, item.getIsme());
        cv.put(message_COLUMN, item.getMessage());
        //cv.put(userId_COLUMN, item.getUserId());
        cv.put(dateTime_COLUMN, item.getDate());
        cv.put(withName_COLUMN, item.getWithName());

        // 新增一筆資料並取得編號
        // 第一個參數是表格名稱
        // 第二個參數是沒有指定欄位值的預設值
        // 第三個參數是包裝新增資料的ContentValues物件
        long id = db.insert("ChatBody", null, cv);

        // 設定編號
        //item.setId(id);
        // 回傳結果
        //return item;
    }

    // 修改參數指定的物件
    public boolean update(ChatMessage item) {
        // 建立準備修改資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的修改資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(isMe_COLUMN, item.getIsme());
        cv.put(message_COLUMN, item.getMessage());
        //cv.put(userId_COLUMN, item.getUserId());
        cv.put(dateTime_COLUMN, item.getDate());
        cv.put(withName_COLUMN, item.getWithName());

        // 設定修改資料的條件為編號
        // 格式為「欄位名稱＝資料」
        String where = withName_COLUMN + "='" + item.getWithName() + "'";

        // 執行修改資料並回傳修改的資料數量是否成功
        return db.update("ChatBody", cv, where, null) > 0;
    }

    // 刪除參數指定編號的資料
    public boolean delete(ChatMessage item){
        // 設定條件為編號，格式為「欄位名稱=資料」
        String where = withName_COLUMN + "='" + item.getWithName() + "'";
        // 刪除指定編號資料並回傳刪除是否成功
        return db.delete("ChatBody", where, null) > 0;
    }

    // 讀取所有記事資料
    public ArrayList<ChatMessage> getAll(String withName) {
        ArrayList<ChatMessage> result = new ArrayList<>();
        Cursor cursor = db.query(true,//distinct
                "ChatBody",//table
                new String[] {KEY_ID, isMe_COLUMN, message_COLUMN, dateTime_COLUMN, withName_COLUMN },//column
                withName_COLUMN + "='" + withName + "'",//where
                null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }
    // 把Cursor目前的資料包裝為物件
    public ChatMessage getRecord(Cursor cursor) {
        // 準備回傳結果用的物件
        ChatMessage result = new ChatMessage();

        result.setMe(cursor.getInt(1) == 1 ? true : false);
        result.setMessage(cursor.getString(2));
        //result.setUserId(cursor.getString(2));
        result.setDate(cursor.getString(3));
        result.setWithName(cursor.getString(4));

        // 回傳結果
        return result;
    }

    // 取得資料數量
    public int getCount(String withName) {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + "ChatBody"
                + " where " + withName_COLUMN + "='" + withName + "'", null);

        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }

        return result;
    }
}
