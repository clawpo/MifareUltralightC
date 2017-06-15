package cn.ucai.mifareultralightc.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by clawpo on 2017/6/9.
 */

public class ProjectDBOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = "ProjectDBOpenHelper";

    private static final int versionNumber = 1;
    private static ProjectDBOpenHelper instance;

    private static final String GIFT_TABLE_CREATE = "CREATE TABLE "
            + ProjectDao.GIFT_TABLE_NAME + " ("
            + ProjectDao.GIFT_COLUMN_NAME + " TEXT, "
            + ProjectDao.GIFT_COLUMN_TIME + " TEXT, "
            + ProjectDao.GIFT_COLUMN_TYPE + " INTEGER, "
            + ProjectDao.GIFT_COLUMN_ID + " INTEGER PRIMARY KEY);";

    public ProjectDBOpenHelper(Context context) {
        super(context, getDatabaseNames(context), null, versionNumber);
    }

    private static String getDatabaseNames(Context context) {
        return context.getPackageName()+".db";
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(GIFT_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    public static ProjectDBOpenHelper getInstance(Context context) {
        if (instance == null) {
            instance = new ProjectDBOpenHelper(context.getApplicationContext());
        }
        return instance;
    }

    public void closeDB() {
        if (instance != null) {
            try {
                SQLiteDatabase db = instance.getWritableDatabase();
                db.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            instance = null;
        }
    }
}
