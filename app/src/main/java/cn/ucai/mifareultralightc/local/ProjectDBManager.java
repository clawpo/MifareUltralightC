package cn.ucai.mifareultralightc.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by clawpo on 2017/6/9.
 */

public class ProjectDBManager {
    private static final String TAG = "ProjectDBManager";
    private static ProjectDBManager dbMgr;
    private ProjectDBOpenHelper dbHelper;

    private ProjectDBManager(Context context){
        dbHelper = ProjectDBOpenHelper.getInstance(context);
        dbMgr = new ProjectDBManager(context);
    }

    public static synchronized ProjectDBManager getInstance(Context context){
        if(dbMgr == null){
            dbMgr = new ProjectDBManager(context);
        }
        return dbMgr;
    }

    /**
     * save gift list
     *
     * @param list
     */
    synchronized public void saveProjectList(List<Project> list) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(ProjectDao.GIFT_TABLE_NAME, null, null);
            for (Project p:list) {
                ContentValues values = new ContentValues();
                values.put(ProjectDao.GIFT_COLUMN_ID, p.getId());
                if(p.getName() != null)
                    values.put(ProjectDao.GIFT_COLUMN_NAME, p.getName());
                if(p.getTime() != null)
                    values.put(ProjectDao.GIFT_COLUMN_TIME, p.getTime());
                values.put(ProjectDao.GIFT_COLUMN_TYPE, p.getType());
                db.replace(ProjectDao.GIFT_TABLE_NAME, null, values);
            }
        }
    }

    /**
     * get contact list
     *
     * @return
     */
    synchronized public Map<Integer, Project> getProjectList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Map<Integer, Project> lists = new Hashtable<Integer, Project>();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + ProjectDao.GIFT_TABLE_NAME /* + " desc" */, null);
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(ProjectDao.GIFT_COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndex(ProjectDao.GIFT_COLUMN_NAME));
                String time = cursor.getString(cursor.getColumnIndex(ProjectDao.GIFT_COLUMN_TIME));
                int type = cursor.getInt(cursor.getColumnIndex(ProjectDao.GIFT_COLUMN_TYPE));
                Project p = new Project(id,name,type,time);
                lists.put(id, p);
            }
            cursor.close();
        }
        return lists;
    }

    synchronized public Project getProject(int id) {
        Project p = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if(db.isOpen()){
            Cursor cursor = db.rawQuery("select * from " + ProjectDao.GIFT_TABLE_NAME
                    +" where " +ProjectDao.GIFT_COLUMN_ID+"=?", new String[]{String.valueOf(id)});
            if(cursor.moveToFirst()){
                String name = cursor.getString(cursor.getColumnIndex(ProjectDao.GIFT_COLUMN_NAME));
                String time = cursor.getString(cursor.getColumnIndex(ProjectDao.GIFT_COLUMN_TIME));
                int type = cursor.getInt(cursor.getColumnIndex(ProjectDao.GIFT_COLUMN_TYPE));
                p = new Project(id,name,type,time);
            }
            cursor.close();
        }
        return p;
    }

    synchronized public Project getProject(String name) {
        Project p = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if(db.isOpen()){
            Cursor cursor = db.rawQuery("select * from " + ProjectDao.GIFT_TABLE_NAME
                    +" where " +ProjectDao.GIFT_COLUMN_NAME+"=?", new String[]{name});
            if(cursor.moveToFirst()){
                int id = cursor.getInt(cursor.getColumnIndex(ProjectDao.GIFT_COLUMN_ID));
                String time = cursor.getString(cursor.getColumnIndex(ProjectDao.GIFT_COLUMN_TIME));
                int type = cursor.getInt(cursor.getColumnIndex(ProjectDao.GIFT_COLUMN_TYPE));
                p = new Project(id,name,type,time);
            }
            cursor.close();
        }
        return p;
    }

    /**
     * save a project
     * @param project
     */
    synchronized public long saveProject(Project project){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ProjectDao.GIFT_COLUMN_TYPE,project.getType());
        if(project.getName() != null)
            values.put(ProjectDao.GIFT_COLUMN_NAME, project.getName());
        if(project.getTime() != null)
            values.put(ProjectDao.GIFT_COLUMN_TIME, project.getTime());
        if(db.isOpen()){
            return db.insert(ProjectDao.GIFT_TABLE_NAME, null, values);
        }
        return -1;
    }
}
